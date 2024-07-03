const express = require('express');
const app = express();
const bodyParser = require('body-parser');
// const path = require('path');
const routes = require ('./routes');

// app.get("/pub/style.css", (req, res) => {
//     res.sendFile("./public/style.css");
// }) Not working because can not find the exact path


// app.get("/pub/style.css", (req, res) => {
//     res.sendFile(path.resolve(__dirname,"./public/style.css"));
// }) working! but problem is if 100 of static files then 100 times this code looping. Not efficient way!
app.use("/pub", express.static("public")); //don't need path right now


app.use(bodyParser.json());
routes(app);

app.listen(4000);
