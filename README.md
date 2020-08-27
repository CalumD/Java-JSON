# Java-Json
This project attempts to fulfill a very specific niche with json-in-java environments.
> "The use of JSON, primarily as a read-only dictionary of information within your Java application."

The origin story; was to design something to cope with an unpredictable JSON structure, without using external 
dependencies (as I was using it as part of my dissertation Uni project).
I also wanted to present a very straight forward API to users with as little a learning curve as possible.

I attempted to follow the official json specification as closely as possible, however I do deviates at some specific
areas which I hope to detail and justify below.


### Features

- Support for json **without a backing Java class representation**.
    - This is one, if not thee, headlining feature. Native, nay, _encouraged_ use of JSON without having to write a Java
    equivalent version of the object.
- Javascript style key-based access to data. (Examples will be shown below)
- Interface-backed methods for parsing, and specific data type retrieval from your JSON.
- Builder support to dynamically build a new JSON object as you go.
