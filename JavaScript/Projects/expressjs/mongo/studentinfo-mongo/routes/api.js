const express = require('express');
const router = express.Router();
const Student = require('../models/student');


router.get('/student', (req, res) => {
  const students = Student.find({}, function(err, students){

        if(err){
            console.log(err);
        }
        else {
            res.json(students);
        }
        // var validate = students.distinct('id');
    });
});

router.post('/student', (req, res, next) => {
  Student.create(req.body).then(student => {
    res.send(student);
  }).catch(next);
});

router.put('/student/:id', (req, res, next) => {
  Student.findByIdAndUpdate({_id: req.params.id}, req.body).then( function() {
    Student.findOne({_id: req.params.id}).then(student => {
      res.send(student);
    });
  });
});

router.delete('/student/:id', (req, res, next) => {
  Student.findByIdAndRemove({_id: req.params.id}).then(student=>{
    res.send(student);
  });
});

module.exports = router
