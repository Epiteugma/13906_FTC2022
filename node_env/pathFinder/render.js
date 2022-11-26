import * as THREE from 'three';
import { OrbitControls } from 'https://unpkg.com/three/examples/jsm/controls/OrbitControls.js';
import { CSS2DObject, CSS2DRenderer } from 'https://unpkg.com/three/examples/jsm/renderers/CSS2DRenderer.js';

const path = require('path');
const child_process = require('child_process');

fetch('data.json').then(r => r.json()).then(data => {
    const scene = new THREE.Scene();
    scene.background = new THREE.Color(0xFFFFFF);
    const camera = new THREE.PerspectiveCamera(75, window.innerWidth / window.innerHeight, 0.1, 1000);
    camera.position.x = -100;
    camera.position.y = 200;
    camera.position.z = -100;
    const renderer = new THREE.WebGLRenderer();
    renderer.setSize(window.innerWidth, window.innerHeight);
    document.body.appendChild(renderer.domElement);

    console.log(data);

    // let obstacles = [...Object.values(data.objects.junctions.ground), ...Object.values(data.objects.junctions.low), ...Object.values(data.objects.junctions.high), ...Object.values(data.objects.junctions.mid), ...Object.values(data.objects.coneStacks)];
    let obstacles = Object.values(data.objects.junctions.high);
    let robot = { x: 15, y: 15, z: 10, w: 25, h: 25, d: 25 };
    let target = { x: 250, y: 120, z: 10 };
    let padding = 0;

    console.log(__dirname);
    let child = child_process.spawn('C:\\Program Files\\Java\\jdk-11\\bin\\java.exe', ['com.z3db0y.flagship.three.Main', JSON.stringify({ obstacles, robot, target, padding })], {
        cwd: path.join(__dirname, '..\\compiled')
    });
    // let tempPoints = [new THREE.Vector3(robot.x, robot.z, robot.y)];
    // let currTempLine = null;
    child.stdout.on('data', data => {
        data = data.toString();
        data.split('\r\n').forEach(line => {
            try {
                let d = JSON.parse(line);
                console.log(d);
                if(d instanceof Array) {
                    console.log(d);
                    let points = [];
                    d.forEach(point => {
                        points.push(new THREE.Vector3(point[0], point[2], point[1]));

                        let geometry = new THREE.BoxGeometry(robot.w, robot.h, robot.d);
                        let material = new THREE.MeshBasicMaterial({ color: 0x00ffff, wireframe: true });
                        let cube = new THREE.Mesh(geometry, material);
                        // cube.position.x = point[0];
                        // cube.position.y = point[2];
                        // cube.position.z = point[1];
                        // scene.add(cube);
                    });
                    let geometry = new THREE.BufferGeometry().setFromPoints(points);
                    let material = new THREE.LineBasicMaterial({ color: 0x00FFFF });
                    let line = new THREE.Line(geometry, material);
                    // scene.add(line);
                    // if(currTempLine) {
                    //     scene.remove(currTempLine);
                    // }
                } else {
                    if(d.type) {
                        // if(tempPoints[tempPoints.length - 1] && tempPoints[tempPoints.length - 1].temp && d.type != 'done') {
                        //     tempPoints.splice(tempPoints.length - 1, 1);
                        // }

                        if(d.type == 'plan') {
                            var mat = new THREE.LineBasicMaterial({ color: 0x00FF00 });
                            var geo = new THREE.BoxGeometry(robot.w, robot.h, robot.d);
                            var cube = new THREE.Mesh(geo, mat);
                            cube.position.x = d.x - 0.5;
                            cube.position.y = d.z - 0.5;
                            cube.position.z = d.y - 0.5;
                            scene.add(cube);
                            // let v = new THREE.Vector3(d.x, d.z, d.y);
                            // v.temp = true;
                            // tempPoints.push(v);
                        } else if(d.type == 'move') {
                            var mat = new THREE.LineBasicMaterial({ color: 0xFFFF00 });
                            var geo = new THREE.BoxGeometry(robot.w, robot.h, robot.d);
                            var cube = new THREE.Mesh(geo, mat);
                            cube.position.x = d.x - 0.5;
                            cube.position.y = d.z - 0.5;
                            cube.position.z = d.y - 0.5;
                            scene.add(cube);
                            // let v = new THREE.Vector3(d.x, d.z, d.y);
                            // tempPoints.push(v);
                        } else if(d.type == 'collision') {
                            var mat = new THREE.LineBasicMaterial({ color: 0xFF0000 });
                            var geo = new THREE.BoxGeometry(robot.w, robot.h, robot.d);
                            var cube = new THREE.Mesh(geo, mat);
                            cube.position.x = d.position.x - 0.5;
                            cube.position.y = d.position.z - 0.5;
                            cube.position.z = d.position.y - 0.5;
                            scene.add(cube);
                        }

                        // if(currTempLine) {
                        //     scene.remove(currTempLine);
                        // }
                        // currTempLine = new THREE.Line(new THREE.BufferGeometry().setFromPoints(tempPoints), new THREE.LineBasicMaterial({ color: 0x00FF00 }));
                        // scene.add(currTempLine);
                        renderer.render(scene, camera);
                    }
                }
            } catch(e) {
                console.log(line);
                console.log(e);
            }
        });
    });
    child.stderr.on('data', data => {
        console.log(data.toString());
    });
    // child.stdio.on('data', d => {
    //     d = d.toString();
    //     try {
    //         d = JSON.parse(d);
    //         if(d.error) {
    //             console.error(d.error);
    //         } else {
    //             let points = [];
    //             d.forEach(point => {
    //                 points.push(new THREE.Vector3(point[0], point[2], point[1]));
    //             });
    //             let geometry = new THREE.BufferGeometry().setFromPoints(points);
    //             let material = new THREE.LineBasicMaterial({ color: 0x00FFFF, linewidth: 5 });
    //             let line = new THREE.Line(geometry, material);
    //             scene.add(line);
    //         }
    //     } catch (e) {
    //         console.log(d);
    //     }
    // });

    const floorGeometry = new THREE.BoxGeometry(366, 1, 366);
    const floorMaterial = new THREE.MeshBasicMaterial({ color: 0xCCCCCC, side: THREE.DoubleSide, wireframe: true });
    const floor = new THREE.Mesh(floorGeometry, floorMaterial);
    floor.position.x = 183;
    floor.position.y = 0;
    floor.position.z = 183;
    scene.add(floor);

    if(data.objects.junctions && data.objects.junctions.ground) for(var junction of Object.values(data.objects.junctions.ground)) {
        var geometry = new THREE.BoxGeometry(junction.w, junction.h, junction.d);
        var material = new THREE.MeshBasicMaterial({ color: 0x000000, wireframe: true });
        var cube = new THREE.Mesh(geometry, material);
        cube.position.x = junction.x;
        cube.position.y = junction.z;
        cube.position.z = junction.y;
        scene.add(cube);
    }

    if(data.objects.junctions && data.objects.junctions.low) for(var junction of Object.values(data.objects.junctions.low)) {
        var geometry = new THREE.BoxGeometry(junction.w, junction.h, junction.d);
        var material = new THREE.MeshBasicMaterial({ color: 0x00FF00, wireframe: true });
        var cube = new THREE.Mesh(geometry, material);
        cube.position.x = junction.x;
        cube.position.y = junction.z;
        cube.position.z = junction.y;
        scene.add(cube);
    }

    if(data.objects.junctions && data.objects.junctions.mid) for(var junction of Object.values(data.objects.junctions.mid)) {
        var geometry = new THREE.BoxGeometry(junction.w, junction.h, junction.d);
        var material = new THREE.MeshBasicMaterial({ color: 0xFFFF00, wireframe: true });
        var cube = new THREE.Mesh(geometry, material);
        cube.position.x = junction.x;
        cube.position.y = junction.z;
        cube.position.z = junction.y;
        scene.add(cube);
    }

    if(data.objects.junctions && data.objects.junctions.high) for(var junction of Object.values(data.objects.junctions.high)) {
        var geometry = new THREE.BoxGeometry(junction.w, junction.h, junction.d);
        var material = new THREE.MeshBasicMaterial({ color: 0xCCCCCC, wireframe: true });
        var cube = new THREE.Mesh(geometry, material);
        cube.position.x = junction.x;
        cube.position.y = junction.z;
        cube.position.z = junction.y;
        scene.add(cube);
    }

    if(data.objects.walls) for(var wall of Object.values(data.objects.walls)) {
        var geometry = new THREE.BoxGeometry(wall.w, wall.h, wall.d);
        var material = new THREE.MeshBasicMaterial({ color: 0xCCCCCC, wireframe: true });
        var cube = new THREE.Mesh(geometry, material);
        cube.position.x = wall.x;
        cube.position.y = wall.z;
        cube.position.z = wall.y;
        scene.add(cube);
    }

    if(data.objects.coneStacks) for(var stack of Object.values(data.objects.coneStacks)) {
        var name = Object.keys(data.objects.coneStacks).find(x => data.objects.coneStacks[x] === stack);
        var geometry = new THREE.BoxGeometry(stack.w, stack.h, stack.d);
        var material = new THREE.MeshBasicMaterial({ color: (name.startsWith('blue') ? 0x0000FF : 0xFF0000), wireframe: true });
        var cube = new THREE.Mesh(geometry, material);
        cube.position.x = stack.x;
        cube.position.y = stack.z;
        cube.position.z = stack.y;
        scene.add(cube);
    }

    let audienceText = document.createElement('div');
    audienceText.innerHTML = 'Audience';
    audienceText.style.fontFamily = 'Orbitron';
    let audienceObject = new CSS2DObject(audienceText);
    audienceObject.position.x = 183;
    audienceObject.position.y = 0;
    audienceObject.position.z = -50;
    scene.add(audienceObject);

    let cssRenderer = new CSS2DRenderer();
    cssRenderer.setSize(window.innerWidth, window.innerHeight);
    cssRenderer.domElement.style.position = 'absolute';
    cssRenderer.domElement.style.top = 0;
    document.body.appendChild(cssRenderer.domElement);

    const controls = new OrbitControls(camera, cssRenderer.domElement);
    controls.target = new THREE.Vector3(183, 0, 183);

    function render() {
        requestAnimationFrame(render);
        controls.update();
        renderer.render(scene, camera);
        cssRenderer.render(scene, camera);
    }
    render();

    document.addEventListener('keydown', (event) => {
        if(event.key == 'r') {
            camera.position.x = 0;
            camera.position.y = 200;
            camera.position.z = 0;
            controls.target = new THREE.Vector3(183, 0, 183);
            controls.update();
        }
    });
});