# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```


Phase 2 Sequence Diagram URL:
https://sequencediagram.org/index.html?presentationMode=readOnly&shrinkToFit=true#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2AMQALADMABwATG4gMP7I9gAWYDoIPoYASij2SKoWckgQaJiIqKQAtAB85JQ0UABcMADaAAoA8mQAKgC6MAD0PgZQADpoAN4ARP2UaMAAtihjtWMwYwA0y7jqAO7QHAtLq8soM8BICHvLAL6YwjUwFazsXJT145NQ03PnB2MbqttQu0WyzWYyOJzOQLGVzYnG4sHuN1E9SgmWyYEoAAoMlkcpQMgBHVI5ACU12qojulVk8iUKnU9XsKDAAFUBhi3h8UKTqYplGpVJSjDpagAxJCcGCsyg8mA6SwwDmzMQ6FHAADWkoGME2SDA8QVA05MGACFVHHlKAAHmiNDzafy7gjySp6lKoDyySIVI7KjdnjAFKaUMBze11egAKKWlTYAgFT23Ur3YrmeqBJzBYbjObqYCMhbLCNQbx1A1TJXGoMh+XyNXoKFmTiYO189Q+qpelD1NA+BAIBMU+4tumqWogVXot3sgY87nae1t+7GWoKDgcTXS7QD71D+et0fj4PohQ+PUY4Cn+Kz5t7keC5er9cnvUexE7+4wp6l7FovFqXtYJ+cLtn6pavIaSpLPU+wgheertBAdZoFByyXAmlDtimGD1OEThOFmEwQZ8MDQcCyxwfECFISh+xXOgHCmF4vgBNA7CMjEIpwBG0hwAoMAADIQFkhRYcwTrUP6zRtF0vQGOo+RoFmipzGsvz-BwVygYKQH+uB5afJCIJqTsXzQo8wHiVQSIwAgQnihignCQSRJgKSb6GLuNL7gyTJTspXI3l5d5LsKYoSm6MpymW7xKpgKrBhqbrarq+r+UYEBqDAaAQMwVpooFvLBZZ1k9n224eZZ-rMtMl7QEgABeKAcFGMZxoUWlJpUolpk4ACMBE5qoebzNBRYlvUaU+DVep1Y1ux0U28XqjAxkAulmXZcwxxgCAqVESgADkAqrQxw4Op1HZWS6G7ulu7kgdUVXTfEs1NS1KCxgp6HwsmyCpjA6b9aMYyDcNBZjGN0D1OKIDQCi4ArfofwmQtp23udxXXRFd3Oh5VLo-Sh5yCgz7xOel7Xmdi6VA+a4BhTOOdu2Omlo54oZKoAGYCzD0SWBhH6fMJGod8FFUfWwu0d9mF-dhMC4fhwP+TRZFjGLiES6RDb0Yx3h+P4XgoOgMRxIkhvG45vhYKJgqgfUDTSBG-ERu0EbdD0cmqApwzq0h0sfuZ-q++g3OB-Cvq4-Utn2FbDlCVbzlqK55WClTo4wIyYCk+TL7aHOQXnTToXik+DPyLK8rB4US2JVqOp6tFRocBlAqbTAeU5AVC4Chd7ndr2-b3b3j2ltVFGvc10YfW1-u-SUYA9UD2b8mDo3FlDjcVlN49QA1TXa02ae81dXb07n8gp55hX8rUHAoNwx6XjnV5513+73sK0j30yhik6+uPMzDvUS2p4OZcx5sPPmLxNIjxlvPHCeEsyo11sxfwKJ1z+GwOKDU-E0QwAAOJKg0DbSqpYGj4Jdu7ewSofaXnFu1WBAdYRBzoRrQoECI6dijmiByaJE4kkvvjAu9IM5MmzhRSmBMe5F3qGFUu59gAV2NKwv2NcbowHiPoLKEAM5ELfkVThJ9+5lSHqQ10z0J7vU+vGDqc9-qAwGivfMa9xoKgsbvOaB80bCJ7pjU+f9GYn1TlI2oyAciEJzBifO19qZClqPgpkBClT-yZhdFmwC+H-gQIBMOx9dLLGoTmAsDRxiFJQAASWkAWXq4RgiBBBJseIuoUBuk5Hsb4yRQBqlaZBRY3wykADklR9IuDAToMC+ZwP+grLMBSiHFNKUqSp1Tan1OWI05pPSDJjA6QgLpWyRo7JBIM4ZOzRnjMbAxTwesAgcAAOxuCcCgJwMQIzBDgFxAAbPACchgImGCKLLMShjJKtA6FQmhz16FZhOXMCZiZKjpOUfBNhaxFlzCGcRLWZlmHh0utZIm6IMRwF+fw5OQ8hExPTpncRZdgDRO7h-UUJcz4v3LlFKucVVS1wwtvWqHimr6IxoYkqA9L5mMlO4vek9WpfVsV1IFi9HG5mcYWdepY+UzQFeuN4XihWLj8fUAJF8KUyBCYSlA-yMRlMkT4plj4knHBSUEtJQCflHhQGA7JodcV5P5mU5Z9Qal1PhRhC63V5aIOBgGqpQbVnIOuagyw99bKbBNkgBIYBk19ggGmgAUhAcUjrDD+E6SANUgL562xHvbJozIZI9DKbQlFSEszYD2cmqAcAIC2SgGsGNoa8VIqrmin4HbKDdt7TRAA6iwcprsegACF+IKDgAAaX6Usqpwtg2BBxV+atXCYAACtC1oAxAW9mKBCRJzcgAq+3cfJZyfhI1+R8QrMolMaxRHKVEhzUUlTVL1tX6t8SK66pVB73olWPfl0qrEz3lfARVAM+rKqGqqiG6qJpSrmtFPV77DWsudYOSlj7RFgCtTat9UimVyOLZFeUMauUJWLetVuOV27Wk7oR8Dp9IPipBaPXDb0p7WIYZM8NKGHHA1BphyGGqRPrjKQR2jkCjEMcCaRs1PjainvFFR5JNG7UfuZNgLQ6JHXABI++RFbrL1oC9Tk316ndKDqmXLGZowE1MX1l4YA8pYgZrNlAALyHgywGANgdthA8gFBgJW8wh6aj20ds7V27tjD+zs7i6BPqD1EZANwPAUTBE6apbfb+j8zyvvkAy9+H6v4P1-nSsradKvNb8jOYzVKmVNZ-jdGzeNyuPrvs1wzcxbW9ca1Vv5RmTXQZy1+eoiAItOfyxZCVIx3NSfgZGxW23LmYCAA
