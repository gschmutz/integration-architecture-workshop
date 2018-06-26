# Setup Infrastructure

## Ubuntu Linux
For the workshops we asume a Ubuntu Linux environment. You can either install that on your machine, use the Virtual Machine image provide with the course or setup a VM in the cloud, for example through the Azure market place. 

### Provisioning an Azure VM with Ubunut installed

## Docker and Docker Compose

### Setup Docker Enine

```
sudo apt-get update

sudo apt-get install \
    apt-transport-https \
    ca-certificates \
    curl \
    software-properties-common
```

```
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
```

```
sudo apt-key fingerprint 0EBFCD88
```

```
sudo add-apt-repository \
   "deb [arch=amd64] https://download.docker.com/linux/ubuntu \
   $(lsb_release -cs) \
   stable"
```

```
sudo apt-get update
```

```
sudo apt-get install docker-ce
```

### Setup Docker Compose

```
sudo curl -L https://github.com/docker/compose/releases/download/1.21.0/docker-compose-$(uname -s)-$(uname -m) -o /usr/local/bin/docker-compose
```

```
sudo chmod +x /usr/local/bin/docker-compose
```
¨
## Java JDK
If you want to install the latest JDK 1.8, use the webupd8team PPA.

Add the repository in your system:

```
sudo add-apt-repository ppa:webupd8team/java
sudo apt-get update
```

You can now install Oracle Java 8 using the following command:

```
sudo apt-get install oracle-java8-installer
```

This ppa repository also provides a package to set environment variables automatically. Just type:

```
sudo apt-get install oracle-java8-set-default

```

## Apache Maven
Install Apache Maven using the following commands:

```
sudo apt-get update
sudo apt-get install maven
```

## Eclipse IDE
Install the Eclipse IDE for Java Developers from here <http://www.eclipse.org/downloads/eclipse-packages/>.

After decompressing the downloaded archive, start the installer by clicking on the **eclipse-inst** icon.

A screen for choosing the version should pop up. Select the Eclipse IDE for Java EE Developers.

![Alt Image Text](./images/eclipse-install-choose-version.png "Schema Registry UI")

On the next screen, select in the **Installation Folder** select the folder where you want Eclipse installed and click on **INSTALL** and the installation will start. 

![Alt Image Text](./images/eclipse-install-start.png "Schema Registry UI")

After a while Eclipse should be ready and can be started. 

## SoapUI

Install the SoapUI Open Source from here <https://www.soapui.org/downloads/soapui.html>.

## SQuirrel SQL

Install the Universal SQL Client SQuirrel SQL from here <http://squirrel-sql.sourceforge.net/>.

## FileZilla CLient

Install the FileZilla Client from here <https://filezilla-project.org/>.

## MQTTfx

Install the MQTT client MQTTfx from here <http://mqttfx.jensd.de/index.php/download>.

## Fish shell
[Fish](https://fishshell.com/) is a smart and user-friendly command line shell for macOS, Linux, and the rest of the family. Fish includes handy features like syntax highlighting, autosuggest-as-you-type, and fancy tab completions that just work, with no configuration required.

Using fish for this course is optional.

Packages for Ubuntu are available from the fish PPA, and can be installed using the following commands:

```
sudo apt-add-repository ppa:fish-shell/release-2
sudo apt-get update
sudo apt-get install fish
```

Once installed, run `fish` from your current shell to try fish out!

If you wish to use fish as your default shell, use the following command:

```
chsh -s /usr/bin/fish
```

`chsh` will prompt you for your password and change your default shell. 