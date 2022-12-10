require('./prompt_polyfill');
const appContainer = document.getElementById('app');
const { config } = require('./common');

alert = (msg) => { require('electron').remote.dialog.showMessageBoxSync(null, { message: msg, title: document.title }) };

let keysdown = [];
let pathstarted = false;
document.addEventListener('keydown', (event) => {
    if(!keysdown.includes(event.key)) keysdown.push(event.key);
});
document.addEventListener('keyup', (event) => {
    keysdown.splice(keysdown.indexOf(event.key), 1);
});

let wrapper = document.createElement('div');
wrapper.id = 'wrapper';
appContainer.appendChild(wrapper);

let canvas = document.createElement('canvas');
canvas.id = 'painter';
canvas.width = config.get('field.width');
canvas.height = config.get('field.height');
let context = canvas.getContext('2d');
context.translate(0.5, 0.5);
let title = document.createElement('h2');
title.innerText = 'Field Preview';
appContainer.appendChild(title);
document.getElementById('wrapper').appendChild(canvas);

let presetTitle = document.createElement('h2');
presetTitle.innerText = 'Presets';
let presetContainer = document.createElement('div');
presetContainer.id = 'presetContainer';

let controlsTitle = document.createElement('h2');
controlsTitle.innerText = 'Controls';
let controls = document.createElement('div');
controls.id = 'controls';
addPathControls();
addObstaclesControls();
addFieldControls();
addExportControls();
appContainer.appendChild(presetTitle);
appContainer.appendChild(presetContainer);
appContainer.appendChild(controlsTitle);
appContainer.appendChild(controls);

let obstacles;
let functions;
drawPath();

String.prototype.format = function(opts) {
    console.log(opts);
    if(typeof opts !== 'object') return this.toString();
    let str = this.toString();
    for(let key of Object.keys(opts)) {
        str = str.replace(new RegExp(`\\$${key}`, 'g'), () => opts[key]);
    }
    return str;
}

function updateFieldSize() {
    canvas.width = config.get('field.width');
    canvas.height = config.get('field.height');
}

function addPresets(quantity) {
    for(let i = 0; i < quantity; i++) {
        let preset = document.createElement('div');
        preset.className = 'preset';

        let presetButtonContainer = document.createElement('div');
        presetButtonContainer.className = 'presetButtonContainer';
        presetButtonContainer.style.width = '100%';
        presetButtonContainer.style.display = 'flex';
        presetButtonContainer.style.justifyContent = 'flex-end';
        preset.appendChild(presetButtonContainer);

        let saveButton = document.createElement('button');
        saveButton.className = 'savePreset';
        saveButton.innerText = 'save';
        saveButton.style.fontFamily = 'Material Symbols Outlined';
        saveButton.addEventListener('click', () => {
            // Todo
            prompt('Test');
        });
        saveButton.style.marginRight = '3px';
        presetButtonContainer.appendChild(saveButton);

        let deleteButton = document.createElement('button');
        deleteButton.className = 'deletePreset';
        deleteButton.innerText = 'delete';
        deleteButton.style.fontFamily = 'Material Symbols Outlined';
        deleteButton.addEventListener('click', () => {
            // Todo
            confirm('Are you sure you want to delete this preset?');
        });
        presetButtonContainer.appendChild(deleteButton);

        presetContainer.appendChild(preset);
    }
}
addPresets(9);

function findClosest45DdegreePoint(point, anchor) {
    let angle = Math.atan2(point.y - anchor.y, point.x - anchor.x);
    if(angle < 0) angle += 2 * Math.PI;
    angle = angle * 180 / Math.PI;

    let closest = 0;
    let closestDiff = 360;
    for(let i = 0; i < 8; i++) {
        let diff = Math.abs(angle - i * 45);
        if(diff < closestDiff) {
            closestDiff = diff;
            closest = i;
        }
    }
    let newAngle = closest * 45 * Math.PI / 180;
    let x = anchor.x + Math.cos(newAngle) * Math.sqrt(Math.pow(point.x - anchor.x, 2) + Math.pow(point.y - anchor.y, 2));
    let y = anchor.y + Math.sin(newAngle) * Math.sqrt(Math.pow(point.x - anchor.x, 2) + Math.pow(point.y - anchor.y, 2));
    return { x, y };
}

let mouse = { x: 0, y: 0 };
function draw() {
    requestAnimationFrame(draw);
    if(document.getElementById('customContextMenu')) return;
    let path = config.get('path');
    context.clearRect(-0.5, -0.5, canvas.width, canvas.height);
    drawObstacles();
    if(!path.length) return;
    for(let i = 0; i < path.length - 1; i++) {
        context.beginPath();
        context.moveTo(path[i].x, path[i].y);
        if(path[i + 1]) {
            context.lineTo(path[i + 1].x, path[i + 1].y);
            context.stroke();
        }
    }
    if(pathstarted) {
        console.log('drawing');
        context.beginPath();
        context.moveTo(path[path.length - 1].x, path[path.length - 1].y);
        if(keysdown.includes('Shift')) mouse = findClosest45DdegreePoint(mouse, path[path.length - 1]);
        context.lineTo(mouse.x, mouse.y);
        let strokeStyle = context.strokeStyle;
        context.strokeStyle = '#aaa';
        context.stroke();
        context.closePath();
        context.strokeStyle = strokeStyle;
    }
}
draw();
canvas.addEventListener('mousemove', (event) => {
    mouse.x = event.offsetX / canvas.clientWidth * canvas.width;
    mouse.y = event.offsetY / canvas.clientHeight * canvas.height;
});

canvas.addEventListener('click', event => {
    if(!pathstarted) {
        clearPath();
        pathstarted = true;
        let points = config.get('path');
        let x = event.offsetX / canvas.clientWidth * canvas.width;
        let y = event.offsetY / canvas.clientHeight * canvas.height;
        points.push({ x, y });
        return;
    }
    else customContextMenu(event);
});

function customContextMenu(event) {
    let percentX = mouse.x / canvas.width;
    let percentY = mouse.y / canvas.height;

    let canvasX = mouse.x;
    let canvasY = mouse.y;

    let container = document.createElement('div');
    container.id = 'customContextMenu';

    let typeSelectContainer = document.createElement('div');
    let typeSelect = document.createElement('select');
    let option1 = document.createElement('option');
    option1.value = 'turn';
    option1.innerText = 'Turn';
    let option2 = document.createElement('option');
    option2.value = 'strafe';
    option2.innerText = 'Strafe';
    typeSelect.appendChild(option1);
    typeSelect.appendChild(option2);
    typeSelectContainer.className = 'ctxControl';
    typeSelectContainer.innerHTML = 'Movement Type';
    typeSelectContainer.appendChild(typeSelect);

    let directionSelectContainer = document.createElement('div');
    let directionSelect = document.createElement('select');
    let option3 = document.createElement('option');
    option3.value = 'forward';
    option3.innerText = 'Forward';
    let option4 = document.createElement('option');
    option4.value = 'backward';
    option4.innerText = 'Backward';
    let option5 = document.createElement('option');
    option5.value = 'left';
    option5.innerText = 'Left';
    let option6 = document.createElement('option');
    option6.value = 'right';
    option6.innerText = 'Right';
    directionSelect.appendChild(option3);
    directionSelect.appendChild(option4);
    directionSelectContainer.className = 'ctxControl';
    directionSelectContainer.innerHTML = 'Direction';
    directionSelectContainer.appendChild(directionSelect);

    typeSelect.addEventListener('change', () => {
        directionSelect.innerHTML = '';
        if(typeSelect.value === 'turn') {
            directionSelect.appendChild(option3);
            directionSelect.appendChild(option4);
        } else {
            directionSelect.appendChild(option5);
            directionSelect.appendChild(option6);
        }
    });

    let velocityContainer = document.createElement('div');
    let velocity = document.createElement('input');
    velocity.type = 'number';
    velocity.value = 1;
    velocityContainer.className = 'ctxControl';
    velocityContainer.innerHTML = 'Velocity';
    velocityContainer.appendChild(velocity);

    let addCommentContainer = document.createElement('div');
    let addComment = document.createElement('input');
    addComment.type = 'text';
    addCommentContainer.className = 'ctxControl';
    addCommentContainer.innerHTML = 'Comment' + '&nbsp;'.repeat(5);
    addCommentContainer.appendChild(addComment);

    let buttonContainer = document.createElement('div');
    buttonContainer.className = 'ctxControl';
    let endPath = document.createElement('button');
    endPath.className = 'endPath';
    endPath.innerText = 'End Path';
    let done = document.createElement('button');
    done.className = 'done';
    done.innerText = 'Done';
    buttonContainer.appendChild(endPath);
    buttonContainer.appendChild(done);

    container.appendChild(typeSelectContainer);
    container.appendChild(directionSelectContainer);
    container.appendChild(velocityContainer);
    container.appendChild(addCommentContainer);
    container.appendChild(buttonContainer);
    
    document.getElementById('wrapper').appendChild(container);
    container.style.position = 'absolute';
    container.style.zIndex = 100;
    container.style.left = `${percentX * 100}%`;
    container.style.top = `${percentY * 100}%`;
    console.log(percentX, percentY);

    done.addEventListener('click', () => {
        let points = config.get('path');
        points.push({
            x: canvasX,
            y: canvasY,
            type: typeSelect.value,
            direction: directionSelect.value,
            velocity: parseFloat(velocity.value),
            comment: addComment.value
        });
        config.set('path', points);
        container.remove();
    });

    endPath.addEventListener('click', () => {
        let points = config.get('path');
        points.push({
            x: canvasX,
            y: canvasY,
            type: typeSelect.value,
            direction: directionSelect.value,
            velocity: parseFloat(velocity.value),
            comment: addComment.value
        });
        config.set('path', points);
        pathstarted = false;
        container.remove();
    });

    let listener = ev => {
        if(!ev.target.closest('#customContextMenu') && ev !== event) {
            container.remove();
            document.removeEventListener('click', listener);
        }
    }
    document.addEventListener('click', listener);
}

function addObstaclesControls() {
    let obsC = document.createElement('div');
    obsC.className = 'control';
    obsC.innerText = 'Obstacles .json';
    let obsHelp = document.createElement('sup');
    obsHelp.innerText = '[?]';
    obsHelp.id = 'obsHelp';
    obsHelp.addEventListener('click', showObstaclePopup);
    obsC.appendChild(obsHelp);
    let obsI = document.createElement('input');
    obsI.type = 'file';
    obsI.accept = '.json';
    obsI.addEventListener('input', handleObstacles);
    obsC.appendChild(obsI);
    controls.appendChild(obsC);
}

function addFieldControls() {
    let fieldC = document.createElement('div');
    fieldC.className = 'control';
    fieldC.innerText = 'Field dimensions';
    let cont = document.createElement('div');
    let fieldW = document.createElement('input');
    fieldW.type = 'number';
    fieldW.value = config.get('field.width');
    fieldW.addEventListener('input', (event) => {
        config.set('field.width', parseFloat(event.target.value));
        updateFieldSize();
    });
    fieldW.style.marginRight = '2px';
    let fieldH = document.createElement('input');
    fieldH.type = 'number';
    fieldH.value = config.get('field.height');
    fieldH.addEventListener('input', (event) => {
        config.set('field.height', parseFloat(event.target.value));
        updateFieldSize();
    });
    cont.appendChild(fieldW);
    cont.appendChild(fieldH);
    fieldC.appendChild(cont);
    controls.appendChild(fieldC);
}

function addExportControls() {
    let robotClassNameC = document.createElement('div');
    robotClassNameC.className = 'control';
    robotClassNameC.innerText = 'Robot class field';
    let robotClassNameI = document.createElement('input');
    robotClassNameI.type = 'text';
    robotClassNameI.value = config.get('robotClassField');
    robotClassNameI.addEventListener('input', (event) => {
        config.set('robotClassField', event.target.value);
    });
    robotClassNameC.appendChild(robotClassNameI);

    let functionFileC = document.createElement('div');
    functionFileC.className = 'control';
    functionFileC.innerText = 'Functions .json';
    let functionFileHelp = document.createElement('sup');
    functionFileHelp.innerText = '[?]';
    functionFileHelp.id = 'functionFileHelp';
    functionFileHelp.addEventListener('click', showFunctionsPopup);
    functionFileC.appendChild(functionFileHelp);
    let functionFileI = document.createElement('input');
    functionFileI.type = 'file';
    functionFileI.accept = '.json';
    functionFileI.addEventListener('input', handleFunctions);
    functionFileC.appendChild(functionFileI);

    let invertTurnC = document.createElement('div');
    invertTurnC.className = 'control';
    invertTurnC.innerText = 'Invert turn';
    let invertTurnSwitch = document.createElement('label');
    invertTurnSwitch.className = 'switch';
    invertTurnSwitch.style.width = '10%';
    invertTurnSwitch.style.display = 'flex';
    let invertTurnInput = document.createElement('input');
    invertTurnInput.type = 'checkbox';
    invertTurnInput.checked = config.get('invertTurn');
    invertTurnInput.addEventListener('input', (event) => config.set('invertTurn', event.target.checked));
    invertTurnInput.style.width = '0';
    invertTurnInput.style.height = '0';
    invertTurnInput.style.margin = '0';
    let invertTurnSlider = document.createElement('span');
    invertTurnSlider.className = 'slider';
    invertTurnSlider.style.width = '100%';
    invertTurnSlider.style.height = '100%';
    invertTurnSwitch.appendChild(invertTurnInput);
    invertTurnSwitch.appendChild(invertTurnSlider);
    invertTurnC.appendChild(invertTurnSwitch);

    let exportBtnC = document.createElement('div');
    exportBtnC.className = 'control';
    let exportBtn = document.createElement('button');
    exportBtn.innerText = 'Export';
    exportBtn.addEventListener('click', exportPath);
    exportBtnC.appendChild(exportBtn);

    controls.appendChild(robotClassNameC);
    controls.appendChild(functionFileC);
    controls.appendChild(invertTurnC);
    controls.appendChild(exportBtnC);
}

function clearPath() {
    config.set('path', []);
    context.clearRect(0, 0, canvas.width, canvas.height);
    drawObstacles();
    pathstarted = false;
}

function handleObstacles(event) {
    let file = event.target.files[0];
    let reader = new FileReader();
    reader.onload = (event) => {
        try {
            let data = JSON.parse(event.target.result);
            if(typeof data === 'object' &&
                typeof data.objects === 'object' &&
                typeof data.objects.junctions === 'object' &&
                typeof data.objects.walls === 'object' &&
                typeof data.objects.coneStacks === 'object' &&
                typeof data.objects.junctions.ground === 'object' &&
                typeof data.objects.junctions.low === 'object' &&
                typeof data.objects.junctions.mid === 'object' &&
                typeof data.objects.junctions.high === 'object' &&
                Object.values(data.objects.junctions.ground).every(v => typeof v === 'object' && typeof v.x === 'number' && typeof v.y === 'number' && typeof v.z === 'number' && typeof v.width === 'number' && typeof v.height === 'number' && typeof v.depth === 'number') &&
                Object.values(data.objects.junctions.low).every(v => typeof v === 'object' && typeof v.x === 'number' && typeof v.y === 'number' && typeof v.z === 'number' && typeof v.width === 'number' && typeof v.height === 'number' && typeof v.depth === 'number') &&
                Object.values(data.objects.junctions.mid).every(v => typeof v === 'object' && typeof v.x === 'number' && typeof v.y === 'number' && typeof v.z === 'number' && typeof v.width === 'number' && typeof v.height === 'number' && typeof v.depth === 'number') &&
                Object.values(data.objects.junctions.high).every(v => typeof v === 'object' && typeof v.x === 'number' && typeof v.y === 'number' && typeof v.z === 'number' && typeof v.width === 'number' && typeof v.height === 'number' && typeof v.depth === 'number') &&
                Object.values(data.objects.walls).every(v => typeof v === 'object' && typeof v.x === 'number' && typeof v.y === 'number' && typeof v.z === 'number' && typeof v.width === 'number' && typeof v.height === 'number' && typeof v.depth === 'number') &&
                Object.values(data.objects.coneStacks).every(v => typeof v === 'object' && typeof v.x === 'number' && typeof v.y === 'number' && typeof v.z === 'number' && typeof v.width === 'number' && typeof v.height === 'number' && typeof v.depth === 'number')
            ) {
                obstacles = data;
                drawObstacles();
                return;
            }
        } catch (e) { console.log(e) }
        alert('Failed to parse obstacles file!');
    };
    reader.readAsText(file);
}

function addPathControls() {
    let pathC = document.createElement('div');
    pathC.className = 'control';
    pathC.innerText = 'Path';
    let cont = document.createElement('div');
    let resetP = document.createElement('button');
    resetP.innerText = 'Reset';
    resetP.addEventListener('click', clearPath);
    cont.appendChild(resetP);
    pathC.appendChild(cont);
    controls.appendChild(pathC);
}

function showObstaclePopup() {
    // TODO: unimportant right now! stop getting distracted
}

function showFunctionsPopup() {
    // TODO: unimportant right now! stop getting distracted
}

function drawObstacles() {
    if(!obstacles) return;
    let fillStyle = context.fillStyle;
    for(var wall of Object.values(obstacles.objects.walls)) {
        context.fillStyle = 'rgb(0, 0, 0)';
        context.fillRect(wall.x - wall.width / 2, wall.y - wall.depth / 2, wall.width, wall.depth);
    }
    for(var coneStack of Object.values(obstacles.objects.coneStacks)) {
        context.fillStyle = 'rgb(255, 0, 0)';
        context.fillRect(coneStack.x - coneStack.width / 2, coneStack.y - coneStack.depth / 2, coneStack.width, coneStack.depth);
    }
    for(var junction of Object.values(obstacles.objects.junctions.ground)) {
        context.fillStyle = 'rgb(0, 255, 0)';
        context.fillRect(junction.x - junction.width / 2, junction.y - junction.depth / 2, junction.width, junction.depth);
    }
    for(var junction of Object.values(obstacles.objects.junctions.low)) {
        context.fillStyle = 'rgb(0, 0, 255)';
        context.fillRect(junction.x - junction.width / 2, junction.y - junction.depth / 2, junction.width, junction.depth);
    }
    for(var junction of Object.values(obstacles.objects.junctions.mid)) {
        context.fillStyle = 'rgb(255, 255, 0)';
        context.fillRect(junction.x - junction.width / 2, junction.y - junction.depth / 2, junction.width, junction.depth);
    }
    for(var junction of Object.values(obstacles.objects.junctions.high)) {
        context.fillStyle = 'rgb(255, 0, 255)';
        context.fillRect(junction.x - junction.width / 2, junction.y - junction.depth / 2, junction.width, junction.depth);
    }

    context.fillStyle = fillStyle;
}

function drawPath() {
    let path = config.get('path');
    if(!path || path.length < 2) return;
    context.beginPath();
    context.moveTo(path[0].x, path[0].y);
    for(var i = 1; i < path.length; i++) {
        context.lineTo(path[i].x, path[i].y);
    }
    context.stroke();
    context.closePath();
}

function handleFunctions(event) {
    let file = event.target.files[0];
    let reader = new FileReader();
    reader.onload = function(e) {
        try {
            let data = JSON.parse(e.target.result);
            if(
                typeof data === 'object' &&
                typeof data.turn === 'string' &&
                typeof data.drive === 'object' &&
                typeof data.drive.forward == 'string' &&
                typeof data.drive.backward == 'string' &&
                typeof data.strafe === 'object' &&
                typeof data.strafe.left == 'string' &&
                typeof data.strafe.right == 'string'
            ) {
                functions = data;
                return;
            }
        } catch (e) { console.log(e) }
        alert('Failed to parse functions file!');
    };
    reader.readAsText(file);
}

async function exportPath() {
    if(!config.get('path') || config.get('path').length < 2) {
        alert('No path to export!');
        return;
    }
    if(!functions) return alert('Please load functions file first!');
    let path = config.get('path');
    let invertTurn = config.get('invertTurn');
    let code = '';
    let className = config.get('robotClassField');

    let currentAngle = 0;
    for(var i = 1; i < path.length; i++) {
        let x = path[i].x - path[i - 1].x;
        let y = path[i].y - path[i - 1].y;
        let velocity = path[i].velocity;
        let direction = path[i].direction;
        let type = path[i].type;
        let comment = path[i].comment;
        let targetAngle = (invertTurn ? -1 : 1) * Math.round(Math.atan2(y, x) * 180 / Math.PI + 90);
        let distance = Math.sqrt(x * x + y * y);

        let prefix = (className || 'this') + '.';
        
        switch(type) {
            case 'turn':
                switch(direction) {
                    case 'forward':
                        if(targetAngle != currentAngle) {
                            code += prefix + functions.turn.format({
                                angle: targetAngle,
                                velocity: velocity
                            });
                            currentAngle = targetAngle;
                            if(!code.endsWith(';')) code += ';';
                            code += '\n';
                        }
                        code += prefix + functions.drive.forward.format({
                            distance: distance,
                            velocity: velocity
                        });
                        if(!code.endsWith(';')) code += ';';
                        code += '\n' + (comment ? '// ' + comment + '\n' : '');
                        break;
                    case 'backward':
                        targetAngle += 180;
                        while(targetAngle > 180) targetAngle -= 360;
                        while(targetAngle < -180) targetAngle += 360;
                        if(targetAngle != currentAngle) {
                            code += prefix + functions.turn.format({
                                angle: targetAngle,
                                velocity: velocity
                            });
                            currentAngle = targetAngle;
                            if(!code.endsWith(';')) code += ';';
                            code += '\n';
                        }
                        code += prefix + functions.drive.backward.format({
                            distance: distance,
                            velocity: velocity
                        });
                        if(!code.endsWith(';')) code += ';';
                        code += '\n' + (comment ? '// ' + comment + '\n' : '');
                        break;
                }
                break;
            case 'strafe':
                switch(direction) {
                    case 'left':
                        targetAngle += 90;
                        while(targetAngle > 180) targetAngle -= 360;
                        while(targetAngle < -180) targetAngle += 360;
                        if(targetAngle != currentAngle) {
                            code += prefix + functions.turn.format({
                                angle: targetAngle,
                                velocity: velocity
                            });
                            currentAngle = targetAngle;
                            if(!code.endsWith(';')) code += ';';
                            code += '\n';
                        }
                        code += prefix + functions.strafe.left.format({
                            distance: distance,
                            velocity: velocity
                        });
                        if(!code.endsWith(';')) code += ';';
                        code += '\n' + (comment ? '// ' + comment + '\n' : '');
                        break;
                    case 'right':
                        targetAngle -= 90;
                        while(targetAngle > 180) targetAngle -= 360;
                        while(targetAngle < -180) targetAngle += 360;
                        if(targetAngle != currentAngle) {
                            code += prefix + functions.turn.format({
                                angle: targetAngle,
                                velocity: velocity
                            });
                            currentAngle = targetAngle;
                            if(!code.endsWith(';')) code += ';';
                            code += '\n';
                        }
                        code += prefix + functions.strafe.right.format({
                            distance: distance,
                            velocity: velocity
                        });
                        if(!code.endsWith(';')) code += ';';
                        code += '\n' + (comment ? '// ' + comment + '\n' : '');
                        break;
                }
                break;
        }
    }

    let exportPath = await require('electron').remote.dialog.showSaveDialog({
        title: 'Export Path',
        defaultPath: 'path.txt'
    });

    if(exportPath.filePath) {
        require('fs').writeFileSync(exportPath.filePath, code);
    } else {
        alert('Export cancelled!');
    }
}