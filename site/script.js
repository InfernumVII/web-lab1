const numberRegex = new RegExp("^-?\\d+\\.?\\d*$")
const chartStep = 176
// const tableHeader = `
// <tr>
//     <th>x</th>
//     <th>y</th>
//     <th>R</th>
//     <th>currentTime</th>
//     <th>timeExecution</th>
//     <th>Success</th>
// </tr>
// `
function checkNumber(text){
    return numberRegex.test(text)
}
function checkNumberBetween(a, b, number){
    return number >= a && number <= b
}
function checkValueWithError(text){
    let errorText = ""
    let success = false
    if (!checkNumber(text)){
        errorText = "Error: value should be number"
    } else {
        if (!checkNumberBetween(-5, 3, text)){
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

function drawPoint(x, y, R){
    const globalStep = chartStep / R
    const pointX = 220 + globalStep * x
    const pointY = 220 - globalStep * y
    const point = document.querySelector(".point")
    point.setAttribute("cx", pointX)
    point.setAttribute("cy", pointY)
}

function submitOnClick(){
    const x = document.querySelector('input[name="X"]:checked').value
    const y = document.querySelector('#input-y').value
    const R = document.querySelector('input[name="R"]:checked').value
    
    var data = {
        x: x,
        y: y, 
        R: R
    }
    fetch("fcgi-app", {
        method: "POST",
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data),
    })
    .then((response) => {
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        drawPoint(parseInt(x), parseFloat(y), parseInt(R))
        return response.text();
    })
    .then((data) => {
        // let tableContent = tableHeader;
        // data.forEach(element => {
        //     tableContent += `
        //     <tr>
        //         <td>${element.cords.x}</td>
        //         <td>${element.cords.y}</td>
        //         <td>${element.cords.R}</td>
        //         <td>${element.currentTimeSeconds}</td>
        //         <td>${element.timeExecution}</td>
        //         <td>${element.success}</td>
        //     </tr>
        //     `
        // });
        document.getElementById("info-table").innerHTML = `<table>${data}</table>`
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