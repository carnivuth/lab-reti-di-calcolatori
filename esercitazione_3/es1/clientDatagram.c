#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <string.h>
#include <ctype.h>
#define LINE_LENGTH 256

int main(int argc, char **argv)
{
	struct hostent *host;
	struct sockaddr_in clientaddr, servaddr;
	int  port, sd, lenServAddr, i=0;
	char fileName[LINE_LENGTH];
	int result;

	/* CONTROLLO ARGOMENTI ---------------------------------- */
	if(argc!=3){
		printf("Error:%s serverAddress serverPort\n", argv[0]);
		exit(1);
	}

	/* INIZIALIZZAZIONE INDIRIZZO CLIENT E SERVER --------------------- */
	memset((char *)&clientaddr, 0, sizeof(struct sockaddr_in));
	clientaddr.sin_family = AF_INET;
	clientaddr.sin_addr.s_addr = INADDR_ANY;

	/* Passando 0 ci leghiamo ad un qualsiasi indirizzo libero,
	* ma cio' non funziona in tutti i sistemi.
	* Se nel nostro sistema cio' non funziona come si puo' fare?
	*/
	clientaddr.sin_port = 0;

	memset((char *)&servaddr, 0, sizeof(struct sockaddr_in));
	servaddr.sin_family = AF_INET;
	host = gethostbyname (argv[1]);

    /* VERIFICA INTERO */
	while( argv[2][i]!= '\0' ){
		if( (argv[2][i] < '0') || (argv[2][i] > '9') ){
			printf("Secondo argomento non intero\n");
			printf("Error:%s serverAddress serverPort\n", argv[0]);
			exit(2);
		}
		i++;
	}
	port = atoi(argv[2]);

	/* VERIFICA PORT e HOST */
	if (port < 1024 || port > 65535){
		printf("%s = porta scorretta...\n", argv[2]);
		exit(2);
	}
	if (host == NULL){
		printf("%s not found in /etc/hosts\n", argv[1]);
		exit(2);
	}else{
		servaddr.sin_addr.s_addr=((struct in_addr *)(host->h_addr))->s_addr;
		servaddr.sin_port = htons(port);
	}

	/* CREAZIONE SOCKET ---------------------------------- */
	sd=socket(AF_INET, SOCK_DGRAM, 0);
	if(sd<0) {perror("apertura socket"); exit(1);}
	printf("Client: creata la socket sd=%d\n", sd);

	/* BIND SOCKET, a una porta scelta dal sistema --------------- */
	if(bind(sd,(struct sockaddr *) &clientaddr, sizeof(clientaddr))<0)
	{perror("bind socket "); exit(1);}
	printf("Client: bind socket ok, alla porta %i\n", clientaddr.sin_port);


    //Corpo del client
    //chiedo all'utente il nome di un file remoto
	lenServAddr=sizeof(servaddr);
    printf("Inserisci il nome di un file o EOF per terminare\n");
	//scanf_s restituisce il numero di valori letti
	//se si inserisce EOF dovrebbe restiruire 0
	while(scanf("%s", fileName)>0){

		//invio il risultato
		if(sendto(sd, fileName, sizeof(fileName), 0, (struct sockaddr *)&servaddr, lenServAddr)<0){
			perror("sendto\n");
			continue;
		}
	
		// ricezione del risultato
		if (recvfrom(sd, &result, sizeof(result), 0, (struct sockaddr *)&servaddr, &lenServAddr)<0){
			perror("recvfrom\n"); 
			continue;
		}
		if (result==-1) printf("Il file non esiste\n");
		else printf("Risposta: %d\n\n", result);
		
		printf("Inserisci il nome di un file o EOF per terminare\n");
	}

	//CLEAN OUT
	close(sd);
	printf("\nClient: termino...\n");  
	exit(0);

}
