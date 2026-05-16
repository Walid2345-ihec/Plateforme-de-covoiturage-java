document.addEventListener('submit',e=>{const b=e.target.querySelector('button.danger');if(b&&!confirm('Confirmer cette action ?'))e.preventDefault();});

function maskCin(cin) {
    if (!cin) return '';
    const str = String(cin).trim();
    if (str.length <= 3) return '*'.repeat(str.length);
    return '*'.repeat(Math.max(1, str.length - 3)) + str.substring(str.length - 3);
}