/**
 * ChesStore - Main JavaScript (AJAX Carrello & Validazione Form DOM)
 */

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

    setTimeout(function() {
        toast.classList.remove('toast-visible');
        toast.addEventListener('transitionend', function() {
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
    }).then(function(response) {
        return response.json().then(function(data) {
            if (!response.ok) {
                throw new Error(data.messaggio || ('Errore ' + response.status));
            }
            return data;
        }).catch(function(err) {
            if (response.ok) {
                throw new Error('Risposta del server non valida.');
            }
            throw err;
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
            button.textContent = 'Aggiunta in corso...';

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
                showToast(err.message || 'Errore durante l\'aggiunta al carrello.', 'error');
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

        var idProdottoInput = form.querySelector('input[name="idProdotto"]');
        var quantitaInput   = form.querySelector('input[name="quantita"]');
        var submitBtn       = form.querySelector('button[type="submit"]');

        if (!idProdottoInput || !quantitaInput || !submitBtn) return;

        var idProdotto  = idProdottoInput.value;
        var quantita    = quantitaInput.value;
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
                feedbackDiv.textContent = err.message || 'Errore di rete. Riprova più tardi.';
                feedbackDiv.classList.add('cart-feedback-error');
            }
            showToast(err.message || 'Errore di rete. Riprova.', 'error');
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

    postToCart(body).then(function(data) {
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
    }).catch(function(err) {
        showToast(err.message || 'Errore di rete. Riprova.', 'error');
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
                showToast(err.message || 'Errore di rete. Riprova.', 'error');
            });
        });
    });
}

/**
 * Validazione Form JavaScript con Regex su eventi change e submit (zero alert, modifica del DOM)
 */
function initAuthFormValidation() {
    const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    const nameRegex = /^[a-zA-ZÀ-ÿ' ]{2,50}$/;

    function setFieldError(field, errorSpan, message) {
        if (!field) return;
        if (message) {
            field.classList.add('input-invalid');
            field.classList.remove('input-valid');
            if (errorSpan) {
                errorSpan.textContent = message;
                errorSpan.style.display = 'block';
            }
        } else {
            field.classList.remove('input-invalid');
            field.classList.add('input-valid');
            if (errorSpan) {
                errorSpan.textContent = '';
                errorSpan.style.display = 'none';
            }
        }
    }

    // 1. Validazione Form Login
    const formLogin = document.getElementById('form-login');
    if (formLogin) {
        const loginEmail = document.getElementById('login-email');
        const loginEmailMsg = document.getElementById('login-email-msg');
        const loginPassword = document.getElementById('login-password');
        const loginPasswordMsg = document.getElementById('login-password-msg');

        function validateLoginEmail() {
            const val = loginEmail.value.trim();
            if (!val) {
                setFieldError(loginEmail, loginEmailMsg, 'L\'email è obbligatoria.');
                return false;
            }
            if (!emailRegex.test(val)) {
                setFieldError(loginEmail, loginEmailMsg, 'Inserisci un formato email valido (es. utente@dominio.it).');
                return false;
            }
            setFieldError(loginEmail, loginEmailMsg, '');
            return true;
        }

        function validateLoginPassword() {
            const val = loginPassword.value;
            if (!val || val.trim().length === 0) {
                setFieldError(loginPassword, loginPasswordMsg, 'La password è obbligatoria.');
                return false;
            }
            setFieldError(loginPassword, loginPasswordMsg, '');
            return true;
        }

        loginEmail.addEventListener('change', validateLoginEmail);
        loginEmail.addEventListener('input', function() {
            if (loginEmail.classList.contains('input-invalid')) validateLoginEmail();
        });

        loginPassword.addEventListener('change', validateLoginPassword);
        loginPassword.addEventListener('input', function() {
            if (loginPassword.classList.contains('input-invalid')) validateLoginPassword();
        });

        formLogin.addEventListener('submit', function(event) {
            const isEmailValid = validateLoginEmail();
            const isPasswordValid = validateLoginPassword();

            if (!isEmailValid || !isPasswordValid) {
                event.preventDefault();
                if (!isEmailValid) loginEmail.focus();
                else loginPassword.focus();
            }
        });
    }

    // 2. Validazione Form Registrazione
    const formReg = document.getElementById('form-registration');
    if (formReg) {
        const regNome = document.getElementById('reg-nome');
        const regNomeMsg = document.getElementById('reg-nome-msg');
        const regCognome = document.getElementById('reg-cognome');
        const regCognomeMsg = document.getElementById('reg-cognome-msg');
        const regEmail = document.getElementById('reg-email');
        const regEmailMsg = document.getElementById('reg-email-msg');
        const regPassword = document.getElementById('reg-password');
        const regPasswordMsg = document.getElementById('reg-password-msg');
        const regConfPassword = document.getElementById('reg-conferma-password');
        const regConfPasswordMsg = document.getElementById('reg-conferma-password-msg');

        function validateNome() {
            const val = regNome.value.trim();
            if (!val) {
                setFieldError(regNome, regNomeMsg, 'Il nome è obbligatorio.');
                return false;
            }
            if (!nameRegex.test(val)) {
                setFieldError(regNome, regNomeMsg, 'Il nome deve contenere almeno 2 lettere.');
                return false;
            }
            setFieldError(regNome, regNomeMsg, '');
            return true;
        }

        function validateCognome() {
            const val = regCognome.value.trim();
            if (!val) {
                setFieldError(regCognome, regCognomeMsg, 'Il cognome è obbligatorio.');
                return false;
            }
            if (!nameRegex.test(val)) {
                setFieldError(regCognome, regCognomeMsg, 'Il cognome deve contenere almeno 2 lettere.');
                return false;
            }
            setFieldError(regCognome, regCognomeMsg, '');
            return true;
        }

        function validateEmail() {
            const val = regEmail.value.trim();
            if (!val) {
                setFieldError(regEmail, regEmailMsg, 'L\'email è obbligatoria.');
                return false;
            }
            if (!emailRegex.test(val)) {
                setFieldError(regEmail, regEmailMsg, 'Formato email non valido (es. nome@dominio.it).');
                return false;
            }
            setFieldError(regEmail, regEmailMsg, '');
            return true;
        }

        function validatePassword() {
            const val = regPassword.value;
            if (!val || val.length < 6) {
                setFieldError(regPassword, regPasswordMsg, 'La password deve avere almeno 6 caratteri.');
                return false;
            }
            setFieldError(regPassword, regPasswordMsg, '');
            if (regConfPassword.value) validateConfermaPassword();
            return true;
        }

        function validateConfermaPassword() {
            const valConf = regConfPassword.value;
            const valPass = regPassword.value;
            if (!valConf) {
                setFieldError(regConfPassword, regConfPasswordMsg, 'Conferma la password inserita.');
                return false;
            }
            if (valConf !== valPass) {
                setFieldError(regConfPassword, regConfPasswordMsg, 'Le password non coincidono.');
                return false;
            }
            setFieldError(regConfPassword, regConfPasswordMsg, '');
            return true;
        }

        regNome.addEventListener('change', validateNome);
        regNome.addEventListener('input', function() { if (regNome.classList.contains('input-invalid')) validateNome(); });

        regCognome.addEventListener('change', validateCognome);
        regCognome.addEventListener('input', function() { if (regCognome.classList.contains('input-invalid')) validateCognome(); });

        regEmail.addEventListener('change', validateEmail);
        regEmail.addEventListener('input', function() { if (regEmail.classList.contains('input-invalid')) validateEmail(); });

        regPassword.addEventListener('change', validatePassword);
        regPassword.addEventListener('input', function() { if (regPassword.classList.contains('input-invalid')) validatePassword(); });

        regConfPassword.addEventListener('change', validateConfermaPassword);
        regConfPassword.addEventListener('input', function() { if (regConfPassword.classList.contains('input-invalid')) validateConfermaPassword(); });

        formReg.addEventListener('submit', function(event) {
            const v1 = validateNome();
            const v2 = validateCognome();
            const v3 = validateEmail();
            const v4 = validatePassword();
            const v5 = validateConfermaPassword();

            if (!v1 || !v2 || !v3 || !v4 || !v5) {
                event.preventDefault();
                if (!v1) regNome.focus();
                else if (!v2) regCognome.focus();
                else if (!v3) regEmail.focus();
                else if (!v4) regPassword.focus();
                else regConfPassword.focus();
            }
        });
    }
}

document.addEventListener('DOMContentLoaded', function() {
    initAddToCartButtons();
    initDetailForm();
    initCartPageAjax();
    initAuthFormValidation();
});
