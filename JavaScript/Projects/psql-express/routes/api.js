const express = require('express');
const router = express.Router();
const client = require('../models/student');


router.get('/student', (req, res) => {
  query = client.query("SELECT * FROM info", (err, results) => {
    if(!err){
      res.send(results.rows);
    } else{
      return res.status(500).json(err);
    }
  });
});

router.post('/student', (req, res, next) => {
  let student = req.body;
  const query = `INSERT INTO info (id, name, cgpa, course) VALUES
                (${student.id}, '${student.name}', '${student.cgpa}', '${student.course}')`;
  client.query(query, (err, results) => {
    if(!err){
      // return res.status(200).json({message: 'New student added successfully'});
      return res.send(req.body);
    } else{
      return res.status(500).json(err);
    }
  });
});

router.put('/student/:id', (req, res, next) => {
  // let student = req.body;
  // let query = `UPDATE info
  //             SET name = '${student.name}',
  //             cgpa = '${student.cgpa}',
  //             course = '${student.course}'
  //             WHERE id = '${student.id}'`;

  const query = `UPDATE info SET course = '${req.body.course}' WHERE id = '${req.params.id}'`;
  client.query(query, (err, results) => {
    if(!err){
      if(results.rowCount == 0){
        console.log(err);
        return res.status(404).json({message: "Student id does not found"});
      }
      return res.status(200).json({message: "Student Information updated successfully"});
    } else{
      console.log(err);
      return res.status(500).json(err);
    }
  });
});

router.delete('/student/:id', (req, res, next) => {
  const query = `DELETE FROM info WHERE id = ${req.params.id}`;
  client.query(query, (err, results) => {
    if(!err){
      if(results.rowCount == 0){
        return res.status(404).json({message: "Student id does not found"});
      }
      return res.status(200).json({message: "Student Information deleted successfully"});
    } else{
      return res.status(500).json(err);
    }
  });
});

module.exports = router;
