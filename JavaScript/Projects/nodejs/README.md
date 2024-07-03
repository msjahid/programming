## Create Project of JavaScript by Yarn
```bash
mkdir nodeproject
cd nodeproject/
yarn init
yarn add express
yarn # if you delete some items then type yarn can back items
touch index.js
```
Expree Code for first time
```JavaScript
const express = require('express')
const app = express()

app.get("/",(req,res)=>{
    res.end("Hello World")
})

app.listen(4000)
```
Debug true mode for developing 
```bash
yarn global add nodemon
#just run your code insted of node to nodemon
```
**Nodemon use in npm or yarn command**:
```bash
sudo npm install -g nodemon
#or
sudo yarn global add nodemon
sudo nodemon -v
```

## Create Project of JavaScript by node
```bash
mkdir nodeproject
cd nodeproject/
npm init -y
npm install express --save
touch index.js
npm install nodemon --save-dev
```
