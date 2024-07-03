const express = require('express');
const router = express.Router();
const db = require('../models/student');
const bodyParser = require('body-parser');



router.get('/student', (req, res) => {
  db.User.findAll().then(students => {
    res.json({data: students})
  }).catch(err => {
    res.json({error: err})
  })
});


router.post('/student', (req, res, next) => {
  // const userInfo = User.build({email: req.body.email, password: req.body.password});
  db.User.create({ email: req.body.email, password: req.body.password }).then(user => {
    res.send(user);
  })
});


router.put('/student/:id', (req, res, next) => {
  db.User.findByPk(req.params.id).then( student => {
    student.update({
      email: req.body.email, password: req.body.password
    }).then((student) => {
      res.send(student);
    });
  });
});


router.delete('/student/:id', (req, res, next) => {
  db.User.findByPk(req.params.id).then(student => {
    student.destroy();
  }).then(student => {
    res.sendStatus(200);
  });
});




module.exports = router
