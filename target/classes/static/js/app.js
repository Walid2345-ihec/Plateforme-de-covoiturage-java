document.addEventListener('submit',e=>{const b=e.target.querySelector('button.danger');if(b&&!confirm('Confirmer cette action ?'))e.preventDefault();});

function maskCin(cin) {
    if (!cin) return '';
    const str = String(cin).trim();
    if (str.length <= 3) return '*'.repeat(str.length);
    return '*'.repeat(Math.max(1, str.length - 3)) + str.substring(str.length - 3);
}

// Registration form validation patterns
const validationPatterns = {
    cin: /^[0-9]{8}$/,
    nom: /^[a-zA-ZÀ-ÿ\s'-]+$/,
    prenom: /^[a-zA-ZÀ-ÿ\s'-]+$/,
    tel: /^[0-9]{8}$/,
    mail: /(?i)^[A-Z0-9._%+-]+@((gmail\.com)|([A-Z0-9.-]+\.tn))$/,
    password: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z0-9]).{8,}$/,
    nomVoiture: /^$|^[a-zA-Z0-9À-ÿ\s'-]+$/,
    matricule: /^$|^[0-9]{1,3}TU[0-9]{4}$/
};

const validationMessages = {
    cin: 'Le CIN est obligatoire et doit contenir exactement 8 chiffres',
    nom: 'Le nom est obligatoire et ne doit contenir que des lettres, espaces, apostrophes et tirets',
    prenom: 'Le prénom est obligatoire et ne doit contenir que des lettres, espaces, apostrophes et tirets',
    tel: 'Le téléphone est obligatoire et doit contenir exactement 8 chiffres',
    anneeUniv: 'L\'année universitaire doit être à partir de 2000',
    adresse: 'L\'adresse est obligatoire',
    mail: 'L\'email est obligatoire et valide (gmail.com ou domaine.tn)',
    password: 'Le mot de passe est obligatoire et doit contenir au moins 8 caractères, 1 minuscule, 1 majuscule, 1 chiffre et 1 caractère spécial',
    nomVoiture: 'Le nom du véhicule ne doit contenir que des lettres, chiffres, espaces, apostrophes et tirets',
    matricule: 'Le matricule doit avoir le format XXXTUYYYYou être vide'
};

const requiredFields = ['cin', 'nom', 'prenom', 'tel', 'adresse', 'mail', 'password'];

document.addEventListener('DOMContentLoaded', function() {
    const form = document.querySelector('form[method="post"][action*="/register"]') || document.querySelector('.grid-form');
    if (!form) return;
    
    // Add real-time validation to input fields
    const inputs = form.querySelectorAll('input, select');
    inputs.forEach(input => {
        input.addEventListener('blur', function() {
            validateField(this);
        });
        input.addEventListener('input', function() {
            if (this.parentElement.querySelector('.error-message')) {
                validateField(this);
            }
        });
    });
    
    // Add form submit validation
    form.addEventListener('submit', function(e) {
        let isValid = true;
        inputs.forEach(input => {
            if (!validateField(input)) {
                isValid = false;
            }
        });
        
        // Check password confirmation
        const password = form.querySelector('input[name="password"]');
        const confirmPassword = form.querySelector('input[name="confirmPassword"]');
        if (password && confirmPassword && password.value && confirmPassword.value && password.value !== confirmPassword.value) {
            confirmPassword.classList.add('input-error');
            if (!confirmPassword.parentElement.querySelector('.error-message')) {
                const errorMsg = document.createElement('span');
                errorMsg.className = 'error-message';
                errorMsg.textContent = 'Les mots de passe ne correspondent pas';
                confirmPassword.parentElement.appendChild(errorMsg);
            }
            isValid = false;
        }
        
        if (!isValid) {
            e.preventDefault();
            // Scroll to first error
            const firstError = form.querySelector('.input-error');
            if (firstError) {
                firstError.scrollIntoView({ behavior: 'smooth', block: 'center' });
            }
        }
    });
});

function validateField(input) {
    const fieldName = input.name;
    const value = input.value.trim();
    const formGroup = input.closest('.form-group');
    
    if (!formGroup) return true;
    
    let isValid = true;
    let errorMsg = '';
    
    // Check if field is required
    const isRequired = requiredFields.includes(fieldName);
    
    if (isRequired && !value) {
        isValid = false;
        errorMsg = getErrorMessage(fieldName, 'required');
    } else if (value) {
        // Validate format
        if (fieldName === 'anneeUniv') {
            const year = parseInt(value);
            if (isNaN(year) || year < 2000) {
                isValid = false;
                errorMsg = validationMessages.anneeUniv;
            }
        } else if (fieldName === 'placesDisponibles') {
            const places = parseInt(value);
            if (isNaN(places) || places < 1 || places > 8) {
                isValid = false;
                errorMsg = 'Le nombre de places doit être entre 1 et 8';
            }
        } else if (fieldName === 'confirmPassword') {
            const password = input.form.querySelector('input[name="password"]');
            if (password && password.value !== value) {
                isValid = false;
                errorMsg = 'Les mots de passe ne correspondent pas';
            }
        } else if (validationPatterns[fieldName]) {
            if (!validationPatterns[fieldName].test(value)) {
                isValid = false;
                errorMsg = validationMessages[fieldName] || 'Format invalide';
            }
        }
    }
    
    // Update UI
    if (isValid) {
        input.classList.remove('input-error');
        const existingError = formGroup.querySelector('.error-message');
        if (existingError) {
            existingError.remove();
        }
    } else {
        input.classList.add('input-error');
        let errorElement = formGroup.querySelector('.error-message');
        if (!errorElement && errorMsg) {
            errorElement = document.createElement('span');
            errorElement.className = 'error-message';
            input.parentElement.insertBefore(errorElement, input.nextSibling);
        }
        if (errorElement) {
            errorElement.textContent = errorMsg;
        }
    }
    
    return isValid;
}

function getErrorMessage(fieldName, type) {
    if (type === 'required') {
        const labels = {
            cin: 'Le CIN est obligatoire',
            nom: 'Le nom est obligatoire',
            prenom: 'Le prénom est obligatoire',
            tel: 'Le téléphone est obligatoire',
            adresse: 'L\'adresse est obligatoire',
            mail: 'L\'email est obligatoire',
            password: 'Le mot de passe est obligatoire'
        };
        return labels[fieldName] || 'Ce champ est obligatoire';
    }
    return validationMessages[fieldName] || 'Format invalide';
}