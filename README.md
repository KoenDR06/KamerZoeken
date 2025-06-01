# Kamer Zoeken

## Running

If you want a command that just downloads, compiles and runs everything, run this: (Assumes you run Linux. It might work on Windows. no guarantees though.) 

```sh
git clone https://github.com/KoenDR06/KamerZoeken
cd KamerZoeken

```

Then, set your preferences in [config.toml](config.toml). After you have done this, run the following script:

```sh
./gradlew build
java -jar build/libs/KamerZoeken-1.0.0.jar
```