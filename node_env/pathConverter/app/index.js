let productcodes = {
    0x0: 'FREE',
    0x1: 'PRO'
};

window.onload = async () => {
    let productcode = await getProductCode();
    let productname = "Auton Generator " + productcodes[productcode];
    document.title = productname;
    document.getElementById('title').innerHTML = productname;

    initEngine(productcode);
};

function initEngine(productcode) {
    require('./engine.' + productcodes[productcode] + '.js');
}