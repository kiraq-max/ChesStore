function getContextPath() {
    if (typeof window.contextPath === 'string') {
        return window.contextPath;
    }
    const meta = document.querySelector('meta[name="context-path"]');
    if (meta && meta.getAttribute('content')) {
        return meta.getAttribute('content');
    }
    const parts = window.location.pathname.split('/');
    if (parts.length > 2 && parts[1] !== 'catalogo' && parts[1] !== 'carrello' && parts[1] !== 'login' && parts[1] !== 'auth' && parts[1] !== 'index.jsp') {
        return '/' + parts[1];
    }
    return '';
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

    setTimeout(function () {
        toast.classList.remove('toast-visible');
        toast.addEventListener('transitionend', function () {
            toast.remove();
        }, { once: true });
    }, 3200);
}

function postToCart(body) {
    var contextPath = getContextPath();
    return fetch(contextPath + '/carrello', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8',
            'X-Requested-With': 'XMLHttpRequest'
        },
        body: body.toString()
    }).then(function (response) {
        return response.json().then(function (data) {
            if (!response.ok) {
                throw new Error(data.messaggio || ('Errore ' + response.status));
            }
            return data;
        }).catch(function (err) {
            if (response.ok) {
                throw new Error('Risposta del server non valida.');
            }
            throw err;
        });
    });
}

function initAddToCartButtons() {
    document.querySelectorAll('.btn-add-cart').forEach(function (button) {
        if (button.closest('form')) return;

        button.addEventListener('click', function (event) {
            event.preventDefault();

            var idProdotto = button.dataset.productId;
            if (!idProdotto) return;

            button.disabled = true;
            var testoOriginale = button.textContent;
            button.textContent = 'Aggiunta in corso...';

            var body = new URLSearchParams({
                action: 'aggiungi',
                idProdotto: idProdotto,
                quantita: '1'
            });

            postToCart(body).then(function (data) {
                if (data.success) {
                    updateCartBadge(data.numeroElementi);
                    showToast('Prodotto aggiunto al carrello!', 'success');
                } else {
                    showToast(data.messaggio || 'Impossibile aggiungere il prodotto.', 'error');
                }
            }).catch(function (err) {
                showToast(err.message || 'Errore durante l\'aggiunta al carrello.', 'error');
            }).finally(function () {
                button.textContent = testoOriginale;
                button.disabled = false;
            });
        });
    });
}

function initDetailForm() {
    var form = document.querySelector('.add-to-cart-form');
    if (!form) return;

    form.addEventListener('submit', function (event) {
        event.preventDefault();

        var idProdottoInput = form.querySelector('input[name="idProdotto"]');
        var quantitaInput = form.querySelector('input[name="quantita"]');
        var submitBtn = form.querySelector('button[type="submit"]');

        if (!idProdottoInput || !quantitaInput || !submitBtn) return;

        var idProdotto = idProdottoInput.value;
        var quantita = quantitaInput.value;
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

        postToCart(body).then(function (data) {
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
        }).catch(function (err) {
            if (feedbackDiv) {
                feedbackDiv.textContent = err.message || 'Errore di rete. Riprova più tardi.';
                feedbackDiv.classList.add('cart-feedback-error');
            }
            showToast(err.message || 'Errore di rete. Riprova.', 'error');
        }).finally(function () {
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
    if (totaleEl) {
        var numTotale = typeof totale === 'number' ? totale : parseFloat(totale);
        totaleEl.textContent = '\u20ac ' + numTotale.toFixed(2).replace('.', ',');
    }
}

function aggiornaQuantitaRiga(idProdotto, nuovaQuantita, riga) {
    var q = parseInt(nuovaQuantita, 10);
    if (isNaN(q) || q < 0) {
        showToast('Quantità non valida.', 'error');
        return;
    }

    var body = new URLSearchParams({
        action: 'modifica',
        idProdotto: idProdotto,
        quantita: q.toString()
    });

    postToCart(body).then(function (data) {
        if (data.success) {
            updateCartSummary(data.numeroElementi, data.totale);

            if (data.quantita <= 0) {
                riga.remove();
                if (data.numeroElementi === 0) {
                    location.reload();
                }
                showToast('Prodotto rimosso dal carrello.', 'success');
            } else {
                var input = riga.querySelector('.cart-qty-input');
                if (input) {
                    input.value = data.quantita;
                }
                var subtotaleEl = riga.querySelector('.cart-subtotal-cell strong');
                if (subtotaleEl) {
                    var sub = typeof data.subtotale === 'number' ? data.subtotale : 0.0;
                    subtotaleEl.textContent = '\u20ac ' + sub.toFixed(2).replace('.', ',');
                }
                showToast('Carrello aggiornato.', 'success');
            }
        } else {
            showToast(data.messaggio || 'Impossibile aggiornare la quantità.', 'error');
        }
    }).catch(function (err) {
        showToast(err.message || 'Errore di rete. Riprova.', 'error');
    });
}

function initCartPageAjax() {
    var cartTable = document.querySelector('.cart-table');
    if (!cartTable) return;

    cartTable.querySelectorAll('.cart-qty-form').forEach(function (form) {
        var input = form.querySelector('.cart-qty-input');
        var idProdotto = form.querySelector('input[name="idProdotto"]').value;
        var riga = form.closest('tr');

        form.addEventListener('submit', function (event) {
            event.preventDefault();
            aggiornaQuantitaRiga(idProdotto, input.value, riga);
        });

        input.addEventListener('change', function () {
            aggiornaQuantitaRiga(idProdotto, input.value, riga);
        });
    });

    cartTable.querySelectorAll('.cart-remove-form').forEach(function (form) {
        form.addEventListener('submit', function (event) {
            event.preventDefault();

            var idProdotto = form.querySelector('input[name="idProdotto"]').value;
            var riga = form.closest('tr');

            var body = new URLSearchParams({
                action: 'rimuovi',
                idProdotto: idProdotto
            });

            postToCart(body).then(function (data) {
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
            }).catch(function (err) {
                showToast(err.message || 'Errore di rete. Riprova.', 'error');
            });
        });
    });
}

document.addEventListener('DOMContentLoaded', function () {
    initAddToCartButtons();
    initDetailForm();
    initCartPageAjax();
    initAuthFormValidation();
});
