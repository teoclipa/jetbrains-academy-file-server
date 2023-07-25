# Java File Server
This project includes a server and a client written in Java that communicate via sockets to store, retrieve, and delete files. The server side of the application manages file storage, while the client side communicates with the server by sending requests and handling responses.

## Features
- Storing, retrieving, and deleting files on the server side.
- Communication between server and client using Sockets.
- Usage of serialization and deserialization to pass custom `Message` objects through sockets.
- Maintaining indexes of files and deleted files for efficient file management.

## Code Structure
- The `server` package includes the server-side code, which consists of the following classes:
  - `Main`: Starts the server and manages incoming client connections.
  - `FileStorage`: Handles file storage-related operations.
  - `FileIndexes`: Manages the indexes of files and deleted files.
  - `SerializeObject`: A utility class for serializing and deserializing objects, and sending and downloading files.
  - `DeserializeObject`: A utility class for deserializing `FileIndexes` objects from the disk.

- The `client` package includes the client-side code, which consists of the following classes:
  - `Main`: Starts the client and manages interaction with the server.
  - `Message`: Defines the structure of messages sent between the client and the server.

## Usage
You need to first start the server by running the `Main` class in the `server` package, and then start the client by running the `Main` class in the `client` package. The client can send `GET`, `PUT`, or `DELETE` requests to the server, which responds accordingly. The communication between the server and client is done through serialized `Message` objects sent over Sockets.

---
