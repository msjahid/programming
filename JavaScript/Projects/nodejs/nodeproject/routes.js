module.exports = function(app) {
    app.get('/', (req, res) => {
        res.end("Welcome, to the JavaScript World!");
    })
    
    app.post('/me', (req, res) => {
        let myAnswer = {
            time: Date.now(),
            greeting: `hello ${req.body.name}! have a good day`,
            id: `student id is ${req.body.id}! available`,
            subject: `student subject in this semester is ${req.body.subject}`,
        }
        // res.end(req.body);
        res.json(myAnswer);
    })
}