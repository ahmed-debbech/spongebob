var express = require('express');
var router = express.Router();
var config = require("../config")
var axios = require("axios")

router.get('/progress', async function(req, res, next) {

  try{
    let re = await axios({
      method: 'get',
      url: config.uploader_host + "/progress"
    })
    res.send(re.data)
  }catch(e){
    console.log("could not get progress of currently uploading video " + e);
    res.send("error")
    return
  }

});


module.exports = router;
