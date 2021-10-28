#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <string.h>
#include <fcntl.h>

int main(int argc, char* argv[]){ //args: ipServ portServ
	
	struct hostent *host;
	struct sockaddr_in serveraddr;
	
	//controllo argomenti
	if (argc != 3){
		perror("Argomenti invalidi\n");
		exit(1);
	}

	host = gethostbyname(argv[1]);
	if (host == NULL){
		perror("Host non presente in /etc/hosts\n");
		exit(2);
	}
	int port = atoi(argv[2]);
	if (port < 1024 || port > 65535){
		perror("Porta invalida\n");
		exit(3);
	}

	//inizialzzazione client e server	
	memset((char *)&serveraddr, 0, sizeof(serveraddr));
	serveraddr.sin_family = AF_INET;
	serveraddr.sin_addr.s_addr = ((struct in_addr *)(host->h_addr))->s_addr;
	serveraddr.sin_port = htons(port);

	//creazione socket
	int sd = socket(AF_INET, SOCK_STREAM, 0); //???
	if (sd<0){
		perror("Errore di apertura socket\n");
		exit(2);
	}

	//bind socket implicita nella connect
	if (connect(sd,(struct sockaddr*)&serveraddr, sizeof(struct sockaddr))<0){
		perror("Errore di binding (connect)\n");
		exit(3);
	}

	char nomeFile[256], buff[256];
	int riga, ok, dim, fd;
	char tmp;
	int len = sizeof(serveraddr);
	
	printf("Inserisci nomeFile\n");
	while (scanf("%s",nomeFile) != EOF){
		printf("Inserisci numero riga da eliminare\n");
		while ((ok = scanf("%d",&riga)) != 1){ //numero dato male
			scanf("%s",buff);
			printf("Inserisci numero riga da eliminare\n");
		}
		printf("outwhile\n");
		
		if ((fd = open(nomeFile,O_RDONLY)) < 0){
			perror("Errore lettura file\n");
			exit(6);
		}

		while(read(fd, &tmp, sizeof(char)) > 0){ //potrebbe volerci > 0, EOF da loop infinito
			printf("CLIENT: letto %c\n", tmp);
			dim++;
		}
	
		nomeFile[strlen(nomeFile)] = '\n'; //cambio \0 con \n per renderlo leggibile dal server
		
		if (sendto(sd, nomeFile, sizeof(nomeFile), 0, (struct sockaddr*)&serveraddr, len) <0){
			perror("Errore di trasmissione\n");
			exit(4);
		}
		if (sendto(sd, &riga, sizeof(riga), 0, (struct sockaddr*)&serveraddr, len) < 0){
			perror("Errore di trasmissione\n");
			exit(4);
		}
		if (sendto(sd, &dim, sizeof(dim), 0, (struct sockaddr*)&serveraddr, len) < 0){
			perror("Errore di trasmissione\n");
			exit(4);
		}
		printf("Inviato nomeFile %s riga %d dim %d\n",nomeFile,riga,dim);
		lseek(fd, SEEK_SET, 0);
		while(read(fd, &tmp, sizeof(char)) > 0){
			write(sd,&tmp,sizeof(tmp)); //ci andrebbe la sendto, è una prova
			printf("CLIENT: inviato %c\n", tmp);
		}
		
		close(fd);

		
	
	}
	shutdown(sd,1); //chiusura output, invio EOF al server
	shutdown(sd,0);
	close(sd);









	return 0;
}
