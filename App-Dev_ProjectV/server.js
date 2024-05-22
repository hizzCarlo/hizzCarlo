// server.js

const express = require('express');
const bodyParser = require('body-parser');
const nodemailer = require('nodemailer');
const mysql = require('mysql');
const bcrypt = require('bcrypt');
const saltRounds = 10; 
const server = express()
server.use(bodyParser.json());

const app = express();
const cors = require('cors')
server.use(cors())


const db = mysql.createConnection({

  host: "localhost",
  user: "root",
  password: "",
  database: "dbgcschool",
});

db.connect(function (error) {
  if (error) {
      console.log("Error connecting to Db");
  } else {
      console.log("Connected!");
  }
});

server.listen(7000, function check(error) {
  if (error) {
      console.log("Error!");
  } else {
      console.log("Started port 7000");
  }
});
// acc
server.post("/api/acc/add", (req, res) => {
  const { username, password, email, name, isAdmin } = req.body;

  // Hash the password before storing it in the database
  bcrypt.hash(password, saltRounds, (err, hash) => {
    if (err) {
      return res.status(500).send({ status: false, message: "Error hashing password" });
    }

    let acc = {
      username: username,
      password: hash, 
      isAdmin: 1
    };

    let sql = "INSERT INTO acc SET ?";
    db.query(sql, acc, (error) => {
        if (error) {
            res.send({ status: false, message: "Account creation failed", error: error });
        } else {
            res.send({ status: true, message: "Account created successfully" });
        }
    });
  });
});
server.get("/api/acc", (req, res) => {
  var sql = "SELECT * FROM acc";
  db.query(sql, function (error, result) {
      if (error) {
          console.log("Error connecting to tbl acc1");
      } else {
          res.send({ status: true, data: result });
      }
  });
});




// Add this endpoint to server.js to fetch the primary userid
server.get("/api/acc/primary", (req, res) => {
  var sql = "SELECT userid FROM acc LIMIT 1";  // This assumes 'userid' is the primary key or unique
  db.query(sql, function (error, result) {
      if (error) {
          console.log("Error fetching primary userid");
          res.send({ status: false, message: "Error fetching primary userid" });
      } else {
          res.send({ status: true, data: result[0].userid });
      }
  });
});

// Endpoint to get user ID by username
server.get("/api/acc/userid", (req, res) => {
  const username = req.query.username;
  const sql = "SELECT userid FROM acc WHERE username = ?";
  db.query(sql, [username], (error, result) => {
      if (error) {
          res.status(500).send({ status: false, message: "Database error" });
      } else if (result.length > 0) {
          res.send({ status: true, data: result[0].userid });
      } else {
          res.status(404).send({ status: false, message: "User not found" });
      }
  });
});

// survey
server.post("/api/survey/add", (req, res) => {
  let survey = {
    titleString: req.body.titleString
  };
  let sql = "INSERT INTO survey SET ?";
  db.query(sql, survey, (error) => {
      if (error) {
          res.send({ status:  false, message: "Survey created Failed" });
      } else {
          res.send({ status: true, message: "Survey Created Successfully" });
      }
  });
});
server.get("/api/survey", (req, res) => {
  var sql = "SELECT * FROM survey";
  db.query(sql, function (error, result) {
      if (error) {
          console.log("Error connecting to tbl survey");
      } else {
          res.send({ status: true, data: result });
      }
  });
});

server.get("/api/survey/:id", (req, res) => {
  var surveyid = req.params.id;
  var sql = "SELECT * FROM survey WHERE id =" + surveyid;
  db.query(sql, function (error, result) {
      if (error) {
          console.log("Error connecting to tbl survey");
      } else {
          res.send({ status: true, data: result });
      }
  });
});

// student
server.post("/api/student/add", (req, res) => {
  const { respondentName, respondentEmail, respondentCourse, respondentOccupation, respondentStatus, surveyTitle } = req.body;

  let studentDetails = {
    respondentName,
    respondentEmail,
    respondentCourse,
    respondentOccupation,
    respondentStatus,
    surveyTitle,
    isAdmin: isAdminGlobal  // Use the global isAdmin status
  };

  let sql = "INSERT INTO student SET ?";
  db.query(sql, studentDetails, (error) => {
    if (error) {
      res.send({ status: false, message: "Student creation failed", error });
    } else {
      res.send({ status: true, message: "Student created successfully" });
    }
  });
});


// Get all students from the database without filtering by isAdmin
server.get("/api/student", (req, res) => {
  var sql = "SELECT * FROM student";
  db.query(sql, function (error, result) {
      if (error) {
          console.log("Error connecting to tbl student");
          res.send({ status: false, message: "Error fetching data" });
      } else {
          res.send({ status: true, data: result });
      }
  });
});

// Get a specific student by id with matching isAdmin in acc
server.get("/api/student/:id", (req, res) => {
  var studentid = req.params.id;
  var sql = "SELECT student.* FROM student JOIN acc ON student.isAdmin = acc.isAdmin WHERE student.id =" + studentid;
  db.query(sql, function (error, result) {
      if (error) {
          console.log("Error connecting to tbl student");
          res.send({ status: false, message: "Error fetching data" });
      } else {
          res.send({ status: true, data: result });
      }
  });
});

server.put("/api/student/update/:id", (req, res) => {
  let sql = 
      "UPDATE student SET respondentName='" +
      req.body.respondentName +
      "' , respondentEmail= '" +
      req.body.respondentEmail +
      "UPDATE student SET respondentCourse='" +
      req.body.respondentCourse+
      "' , respondentOccupation= '" +
      req.body.respondentOccupation +
      "UPDATE student SET respondentStatus='" +
      req.body.respondentStatus+
      "UPDATE student SET surveyTitle='" +
      req.body.surveyTitle+
      "' WHERE id=" +
      req.params.id;

  let a = db.query(sql, (error, result) => {
      if (error) {
          res.send({ status: false, message: "Failed to update data" });
      } else {
          res.send({ status: true, message: "Data updated successfully." });
      }
  });
});

server.delete("/api/student/delete/:id", (req, res) => {
  let sql = "DELETE FROM student WHERE id=" + req.params.id + "";
  let query = db.query(sql, (error) => {
      if (error) {
          res.send({ status: false, message: "Failed to delete data" });
      } else {
          res.send({ status: true, message: "Successfully deleted the data" });
      }
  });
});

// Middleware
app.use(bodyParser.json());

// Endpoint for sending emails
app.post('/send-email', (req, res) => {
  const { subject, body, to } = req.body;

  if (!subject || !body || !to) {
    return res.status(400).json({ message: 'Missing required fields' });
  }

  // Create transporter
  const transporter = nodemailer.createTransport({
    service: 'Gmail',
    auth: {
      user: 'fitnezzgo@gmail.com', // Replace with your Gmail email address
      pass: 'meowmelol' // Replace with your Gmail password
    }
  });

  // Email options
  const mailOptions = {
    from: 'fitnezzgo@gmail.com', // Replace with your Gmail email address
    to,
    subject,
    text: body
  };

  // Send email
  transporter.sendMail(mailOptions, (error, info) => {
    if (error) {
      console.error('Error sending email:', error);
      return res.status(500).json({ message: 'Failed to send email' });
    } else {
      console.log('Email sent:', info.response);
      return res.status(200).json({ message: 'Email sent successfully' });
    }
  });
});

// Start server
const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`Server is running on port ${PORT}`);
});

let isAdminGlobal = false; // Variable to store isAdmin status

server.post('/api/admin-status', (req, res) => {
  isAdminGlobal = req.body.isAdmin;
  res.send({ status: true, message: 'Admin status updated successfully' });
});
