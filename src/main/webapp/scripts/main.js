function getContextPath() {
    const parts = window.location.pathname.split('/');
    return parts.length > 1 ? '/' + parts[1] : '';
}

function updateCartBadge(count) {
    const badge = document.getElementById('cart-badge');
    if (!badge) return;
    badge.textContent = count;
}

function showToast(messaggio, tipo) {
    tipo = tipo || 'success';

    let container = document.getElementById('toast-container');
    if (!container) {
        container = document.createElement('div');
        container.id = 'toast-container';
        document.body.appendChild(container);
    }

    const toast = document.createElement('div');
    toast.className = 'toast toast-' + tipo;
    toast.textContent = messaggio;

    container.appendChild(toast);

    void toast.offsetWidth;
    toast.classList.add('toast-visible');

    setTimeout(function() {
        toast.classList.remove('toast-visible');
        toast.addEventListener('transitionend', function() {
            toast.remove();
        }, { once: true });
    }, 3000);
}

function postToCart(body) {
    var contextPath = getContextPath();
    return fetch(contextPath + '/carrello', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'X-Requested-With': 'XMLHttpRequest'
        },
        body: body.toString()
    }).then(function(response) {
        return response.json().then(function(data) {
            if (!response.ok) {
                throw new Error(data.messaggio || 'Errore ' + response.status);
            }
            return data;
        });
    });
}

function initAddToCartButtons() {
    document.querySelectorAll('.btn-add-cart').forEach(function(button) {
        if (button.closest('form')) return;

        button.addEventListener('click', function(event) {
            event.preventDefault();

            var idProdotto = button.dataset.productId;
            if (!idProdotto) return;

            button.disabled = true;
            var testoOriginale = button.textContent;
            button.textContent = 'Aggiunta...';

            var body = new URLSearchParams({
                action: 'aggiungi',
                idProdotto: idProdotto,
                quantita: '1'
            });

            postToCart(body).then(function(data) {
                if (data.success) {
                    updateCartBadge(data.numeroElementi);
                    showToast('Prodotto aggiunto al carrello!', 'success');
                } else {
                    showToast(data.messaggio || 'Impossibile aggiungere il prodotto.', 'error');
                }
            }).catch(function(err) {
                showToast(err.message || 'Errore di rete. Riprova.', 'error');
            }).finally(function() {
                button.textContent = testoOriginale;
                button.disabled = false;
            });
        });
    });
}

function initDetailForm() {
    var form = document.querySelector('.add-to-cart-form');
    if (!form) return;

    form.addEventListener('submit', function(event) {
        event.preventDefault();

        var idProdotto  = form.querySelector('input[name="idProdotto"]').value;
        var quantita    = form.querySelector('input[name="quantita"]').value;
        var submitBtn   = form.querySelector('button[type="submit"]');
        var feedbackDiv = document.getElementById('cart-feedback-' + idProdotto);

        submitBtn.disabled = true;
        var testoOriginale = submitBtn.innerHTML;
        submitBtn.innerHTML = '&#128722; Aggiunta...';

        if (feedbackDiv) {
            feedbackDiv.textContent = '';
            feedbackDiv.className = 'cart-feedback-msg';
        }

        var body = new URLSearchParams({
            action: 'aggiungi',
            idProdotto: idProdotto,
            quantita: quantita
        });

        postToCart(body).then(function(data) {
            if (data.success) {
                updateCartBadge(data.numeroElementi);
                if (feedbackDiv) {
                    feedbackDiv.textContent = '\u2713 Prodotto aggiunto al carrello!';
                    feedbackDiv.classList.add('cart-feedback-success');
                }
                showToast('Prodotto aggiunto al carrello!', 'success');
            } else {
                if (feedbackDiv) {
                    feedbackDiv.textContent = data.messaggio || 'Impossibile aggiungere il prodotto.';
                    feedbackDiv.classList.add('cart-feedback-error');
                }
                showToast(data.messaggio || 'Impossibile aggiungere il prodotto.', 'error');
            }
        }).catch(function(err) {
            if (feedbackDiv) {
                feedbackDiv.textContent = 'Errore di rete. Riprova pi\u00f9 tardi.';
                feedbackDiv.classList.add('cart-feedback-error');
            }
            showToast('Errore di rete. Riprova.', 'error');
        }).finally(function() {
            submitBtn.innerHTML = testoOriginale;
            submitBtn.disabled = false;
        });
    });
}

function updateCartSummary(numeroElementi, totale) {
    updateCartBadge(numeroElementi);

    var contatoreEl = document.getElementById('summary-count');
    if (contatoreEl) contatoreEl.textContent = numeroElementi;

    var totaleEl = document.getElementById('summary-total');
    if (totaleEl) totaleEl.textContent = '\u20ac ' + totale.toFixed(2).replace('.', ',');
}

function aggiornaQuantitaRiga(idProdotto, nuovaQuantita, riga) {
    var body = new URLSearchParams({
        action: 'modifica',
        idProdotto: idProdotto,
        quantita: nuovaQuantita
    });

    postToCart(body).then(function(data) {
        if (data.success) {
            updateCartSummary(data.numeroElementi, data.totale);

            var prezzoCellText = riga.querySelector('.cart-price-cell') ? riga.querySelector('.cart-price-cell').textContent : '';
            var prezzoMatch = prezzoCellText.match(/[\d]+[.,]?\d*/);
            if (prezzoMatch) {
                var prezzoUnitario = parseFloat(prezzoMatch[0].replace(',', '.'));
                var subtotale = prezzoUnitario * parseInt(nuovaQuantita, 10);
                var subtotaleEl = riga.querySelector('.cart-subtotal-cell strong');
                if (subtotaleEl) {
                    subtotaleEl.textContent = '\u20ac ' + subtotale.toFixed(2).replace('.', ',');
                }
            }

            showToast('Carrello aggiornato.', 'success');
        } else {
            showToast(data.messaggio || 'Impossibile aggiornare la quantit\u00e0.', 'error');
        }
    }).catch(function(err) {
        showToast('Errore di rete. Riprova.', 'error');
    });
}

function initCartPageAjax() {
    var cartTable = document.querySelector('.cart-table');
    if (!cartTable) return;

    cartTable.querySelectorAll('.cart-qty-form').forEach(function(form) {
        var input      = form.querySelector('.cart-qty-input');
        var idProdotto = form.querySelector('input[name="idProdotto"]').value;
        var riga       = form.closest('tr');

        form.addEventListener('submit', function(event) {
            event.preventDefault();
            aggiornaQuantitaRiga(idProdotto, input.value, riga);
        });

        input.addEventListener('change', function() {
            aggiornaQuantitaRiga(idProdotto, input.value, riga);
        });
    });

    cartTable.querySelectorAll('.cart-remove-form').forEach(function(form) {
        form.addEventListener('submit', function(event) {
            event.preventDefault();

            var idProdotto = form.querySelector('input[name="idProdotto"]').value;
            var riga = form.closest('tr');

            var body = new URLSearchParams({
                action: 'rimuovi',
                idProdotto: idProdotto
            });

            postToCart(body).then(function(data) {
                if (data.success) {
                    riga.remove();
                    if (data.numeroElementi === 0) {
                        location.reload();
                    }
                    updateCartSummary(data.numeroElementi, data.totale);
                    showToast('Prodotto rimosso dal carrello.', 'success');
                } else {
                    showToast(data.messaggio || 'Impossibile rimuovere il prodotto.', 'error');
                }
            }).catch(function(err) {
                showToast('Errore di rete. Riprova.', 'error');
            });
        });
    });
}

document.addEventListener('DOMContentLoaded', function() {
    initAddToCartButtons();
    initDetailForm();
    initCartPageAjax();
});
