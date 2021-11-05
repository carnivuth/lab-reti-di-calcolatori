#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <string.h>
#include <arpa/inet.h>

int main(int argc, char* argv[]){ //args: ipServer portServer

	struct hostent *host;
	struct sockaddr_in clientaddr, serveraddr;

	//controllo argomenti
	if (argc != 3){
		perror("Argomenti invalidi\n");
		exit(1);
	}

	int port = atoi(argv[2]);
	if (port < 1024 || port > 65535){
		perror("Porta invalida\n");
		exit(2);
	}

	host = gethostbyname(argv[1]);
	if (host == NULL){
		perror("Host invalido\n");
		exit(3);
	}

	//inizializzazione address client
	memset((char *)&clientaddr, 0, sizeof(clientaddr));
	clientaddr.sin_family = AF_INET;
	clientaddr.sin_port = 0;
	clientaddr.sin_addr.s_addr = INADDR_ANY;
	
	//iniziializzazione address server
	memset((char *)&serveraddr, 0, sizeof(serveraddr));
	serveraddr.sin_family = AF_INET;
	serveraddr.sin_addr.s_addr = ((struct in_addr *)(host->h_addr))->s_addr;
	serveraddr.sin_port = htons(port);

	//creazione socket e settaggio socket
	int sd = socket(AF_INET, SOCK_DGRAM, 0);
	if (sd < 0){
		perror("Errore creazione socket\n");
		exit(4);
	}
	if (bind(sd, (struct sockaddr *)&clientaddr, sizeof(clientaddr)) < 0){
		perror("Errore binding\n");
		exit(5);
	}
	
	//PERCHE FAI LA CONNECT CON UNA SOCK DATAGRAM?????????
	//if (connect(sd, (struct sockaddr *)&serveraddr, sizeof(serveraddr)) < 0){
	//	perror("Errore connect\n");
	//	exit(6);
	//}
	

	//main loop
	char nomeFile[256];
	char parola[256];
	char buff[256];
	int intVal, intValNet, ris;
	printf("Inserisci nome file\n");
	while(scanf("%s", nomeFile) > 0){
		printf("Inserisci parola da eliminare\n");
		if (scanf("%s", parola) <= 0){
			perror("Errore di input\n");
			printf("Inserisci nome file\n");
			continue;
		}
		
		printf("Writing to: %s:%d\n", inet_ntoa(serveraddr.sin_addr), ntohs(serveraddr.sin_port));

/*
		//invio dimFile e file
		intVal = strlen(nomeFile)+1; //\0 incluso
		intValNet = htonl(intVal);
		write(sd, &intValNet, sizeof(int));
		write(sd, nomeFile, intVal);

		//invio dimParola e parola
		intVal = strlen(parola)+1;
		intValNet = htonl(intVal);
		write(sd, &intValNet, sizeof(int));
		write(sd, parola, intVal);
*/
/*		intVal = strlen(nomeFile)+1;
		intValNet = htonl(intVal);
		memcpy(buff,&intValNet,sizeof(int));
		strcpy(buff+4,nomeFile);
		intVal = strlen(parola)+1;
		intValNet = htonl(intVal);
		memcpy(buff+4+strlen(nomeFile),&intValNet,sizeof(int));
		strcpy(buff+4+strlen(nomeFile)+4,parola);
		write(sd, buff, 8+strlen(nomeFile)+strlen(parola));
*/
		memcpy(buff,nomeFile,strlen(nomeFile)+1);
		memcpy(buff+strlen(nomeFile)+1, parola, strlen(parola)+1);
		//inviare solo lunghezza esatta parola invece che tutto il buffer
		write(sd, buff, 256);

		//ricezione risultato
		read(sd, &ris, sizeof(int));
		ris = ntohl(ris);
		printf("Risposta: %d\n", ris);
		
		printf("Inserisci nome file\n");
	}






}
