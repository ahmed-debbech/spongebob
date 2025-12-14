var express = require('express');
var router = express.Router();
const fs = require("fs")
var config = require("../config")
var axios = require("axios")

/* GET home page. */
router.get('/', function(req, res, next) {
  res.render('index', { title: 'SpongeBob', google_redirect_uri: encodeURI(config.google_redirect_uri) });
});

router.get('/oauth', async function(req, res, next) {

  try{
    await axios({
      method: 'get',
      url: config.uploader_host + "/oauth?code=" + req.query.code
    })
  }catch(e){
    console.log("could not send code back from google to uploader service, it seems " + e);
      res.json({
        "authed" : false,
        "error" : e.toString()
      })
      return
  }

  res.json({
    "authed" : true
  })

});

router.post('/setPlaylist', async function(req, res, next) {

  try{
    let d = await axios({
      method: 'post',
      url: config.scdown_host + "/setPlaylist",
      data: req.body
    })

    res.json({
        "error" : false,
        "tracklist" : d.data
    })
      return
  }catch(e){
    console.log("it seems " + e);
      res.status(400)
      res.json({
        "error" : true,
        "why" : e.toString(),
        "reason" : e.response.data
      })
    return
  }

});

router.get('/downloadPlaylist', async function(req, res, next) {

  try{
    let d = await axios({
      method: 'get',
      url: config.scdown_host + "/downloadPlaylist",
    })

    res.json({
        "error" : false
    })
      return
  }catch(e){
    console.log("it seems " + e);
      res.status(400)
      res.json({
        "error" : true,
        "why" : e.toString(),
        "reason" : e.response.data
      })
    return
  }

});


router.get('/getFile', (req, res) => {

      
  let singleFile = ""
  fs.readdirSync("./scdownloads").forEach(file => {
      singleFile = file
  });

  // Get file stats
  fs.stat("./scdownloads/"+singleFile, (err, stats) => {
    if (err) {
      console.log(err)
      return res.status(404).send('File not found');
    }
    
    // Set headers
    res.setHeader('Content-Length', stats.size);
    res.setHeader('Content-Type', 'application/zip');
    res.setHeader('Content-Disposition', 'attachment; filename='+singleFile);
    
    // Stream the file
    const stream = fs.createReadStream("./scdownloads/" + singleFile);
    stream.pipe(res);
  });
});

module.exports = router;
