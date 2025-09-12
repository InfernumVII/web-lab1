const numberRegex = new RegExp("^-?\\d+$")
const tableHeader = `
<tr>
    <th>x</th>
    <th>y</th>
    <th>R</th>
    <th>currentTime</th>
    <th>timeFromStart</th>
    <th>Success</th>
</tr>
`
function checkDecimal(text){
    return numberRegex.test(text)
}
function checkNumberBetween(a, b, number){
    return number >= -5 && number <= 3
}
function checkValueWithError(text){
    let errorText = ""
    let success = false
    if (!checkDecimal(text)){
        errorText = "Error: value should be number"
    } else {
        if (!checkNumberBetween(-5, -3, text)){
            errorText = "Error: value should be between -5 and 3"
        } else {
            success = true
        }
    }
    return [success, errorText]
}
function onInputTextUpdate(text){
    let validation = checkValueWithError(text)
    document.getElementById("input-text-error").innerHTML = validation[1];
    document.querySelector("#button-submit").disabled = !validation[0]
}

function submitOnClick(){
    var data = {
        x: parseInt(document.querySelector('input[name="X"]:checked').value),
        y: parseInt(document.querySelector('#input-y').value), 
        R: parseInt(document.querySelector('input[name="R"]:checked').value)
    }
    fetch("fcgi-app", {
        method: "POST",
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data),
    })
    .then((response) => response.json())
    .then((data) => {
        let tableContent = tableHeader;
        data.forEach(element => {
            tableContent += `
            <tr>
                <td>${element.cords.x}</td>
                <td>${element.cords.y}</td>
                <td>${element.cords.R}</td>
                <td>${element.timeFromStartSeconds}</td>
                <td>${element.currentTimeSeconds}</td>
                <td>${element.success}</td>
            </tr>
            `
        });
        document.getElementById("info-table").innerHTML = `<table>${tableContent}</table>`
    })
    .catch((error) => {
       console.error(error); 
    });
}

function createParticles() {
    const particlesContainer = document.getElementById('particles');
    const particleCount = 30;
    for (let i = 0; i < particleCount; i++) {
        const particle = document.createElement('div');
        particle.classList.add('particle');
        const size = Math.random() * 10 + 2;
        particle.style.width = `${size}px`;
        particle.style.height = `${size}px`;
        particle.style.left = `${Math.random() * 100}vw`;
        particle.style.top = `${Math.random() * 100}vh`;
        particle.style.animationDelay = `${Math.random() * 15}s`;
        

        particle.style.opacity = Math.random() * 0.5;
        
        particlesContainer.appendChild(particle);
    }
}

window.addEventListener('load', createParticles);