
# SpongeBob - Filter tool for copyrights

## Motivations

You know Youtube? you probably know it always complains about copyrights for audio you use. 

My friend is a DJ and he records songs mixes and uploads them to Youtube. Every time he does that he ends up with several copyright claims which makes his channel unmonitizable. 

Somehow i could get what it needs for him to decide which songs to use: \
1 - find songs he likes from soundcloud (whether they are copyrighted or not) \
2 - download them \
3 - create a video that contains these songs as a test. \
4 - upload the test video to Youtube as private video \
5 - check which songs are copyrighted and not use them in the mix. 

So this is my attempt to provide a way to automate all of this...

## Why Sponge Bob?

Initially the name was just "Sponge" because sponges in the sea filters things. But "Sponge" is not an attractive name. So we settled on SpongeBob, a tool that has nothing to do to with SpongeBob whatsoever. 

## What does it do?

SpongeBob gets a collection of songs from you and downloads it and tests it against youtube for copyright claims by uploading a private test video containing all of the collection's songs, then it marks which songs are copyrighted.

## Is it made for you?

Most likely if you are using Youtube as a creator, you would need a way to tell if a piece of audio is copyrighted or not to be able to use it.

## How to use it?

Because the platform was tailored to my friend's needs, it now only accepts Soundcloud playlist links as a song collection to input to the platform to start the testing. You can visit it the page here.

### For developers:
The whole platform is configurable from either `docker-compose.yaml` file or any `Config.java`/`Config.js` file and no more.

You can run `make` to launch the system and make it up and running, or `make clean` to delete data directories and start fresh

## Project Structure

### The flow

1 - You set the Soundcloud playlist link in the web page. \
2 - SpongeBob will try to fetch songs in the playlist and download them in `.mp3` format on server \
3 - Spongebob will take the downloaded collection and generate an `.mp4` video with the mp3 files set in order with black image. \
4 - Spongebob will upload that `.mp4` video to youtube to the selected youtube channel from the web page. \
5 - SpongeBob will check what Youtube gave as a result for copyright claims on the video and mark the copyrighted songs on the web page.

### Structure

It is all about microservices even though it is self hostable and supports a single user per instance. I just find it easier for me to work on the different parts of the system and link them with queues 

1 - **Dash** (nodejs) service to render the web page dashboard \
2 - **Scdown** (java) serves as a backend for dash that downloads the songs from soundcloud and prepares the mp3 files. \
3 - **main** (java) simple service to generate a mp4 video from the mp3 files \
4 - **uploader** (java) responsible for uploading any mp4 video file to youtube. \
5 - we still missing the checker service as it is still getting written that will check the uploaded video and claim which songs are copyrighted. 

Note: To connect all these services together we need a standard way to communicate, RabbitMQ will do all the job queuing to ensure that the pipeline is seamless.  

Note: Redis will be used only by **scdown** to store the songs library both on memory and disk.

## Roadmap

* We need to start working on the copyright checker. this service should reply back in queue to **scdown** about the songs to consider clean or copyrighted.

### Project Status

Work in progress by Ahmed Debbech.

