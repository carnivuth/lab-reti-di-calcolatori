#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <string.h>
#include <fcntl.h>
#include <time.h>

int main(int argc, char* argv[]){ //args: ipServ portServ
	struct timeval time;
	struct hostent *host;
	struct sockaddr_in serveraddr;
	time.tv_sec=0;
	time.tv_usec=0;

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
	int riga, ok, dim, fd, fd2;
	char tmp;
	int len = sizeof(serveraddr);
	
	printf("Inserisci nomeFile\n");
	while (scanf("%s",nomeFile) != EOF){
		printf("Inserisci numero riga da eliminare\n");
		while ((ok = scanf("%d",&riga)) != 1){ //numero dato male
			scanf("%s",buff);
			printf("Inserisci numero riga da eliminare\n");
		}
		
		//controllo l'esistenza del file
		if ((fd = open(nomeFile,O_RDONLY)) < 0){
			printf("File non esistente \nInserisci nomeFile\n");
			continue;
		}

		//conto la dimensione del file
		dim=lseek(fd, 0, SEEK_END);
		lseek(fd, 0, SEEK_SET);
		
		//invio il nome del file
		if (sendto(sd, nomeFile, sizeof(nomeFile), 0, (struct sockaddr*)&serveraddr, len) <0){
			perror("Errore di trasmissione\n");
			exit(4);
		}

		//invio la riga da eliminare
		if (sendto(sd, &riga, sizeof(riga), 0, (struct sockaddr*)&serveraddr, len) < 0){
			perror("Errore di trasmissione\n");
			exit(4);
		}

		//invio la dimensione del file
		if (sendto(sd, &dim, sizeof(dim), 0, (struct sockaddr*)&serveraddr, len) < 0){
			perror("Errore di trasmissione\n");
			exit(4);
		}
		printf("Inviato nomeFile %s riga %d dim %d\n",nomeFile,riga,dim);
		
		//nome del file di appoggio = pid del client
		char tmpFileName[20];
		sprintf(tmpFileName, "%d.txt", getpid());
		//apro un file su cui scrivere cio' che mi invia il server
		if ((fd2 = open(tmpFileName, O_CREAT|O_RDWR, 0666)) < 0){
			printf("Non riesco a creare il file di appoggio\n");
			exit(5);
		}

		buff[0]='\0';
		int countLinee=0;
		int letti=0;
		
		int nfds=7;
		//int* read_mask;
		fd_set read_mask;
		FD_ZERO(&read_mask);
		
		//lettura byte per byte
		while(read(fd, &tmp, sizeof(char)) > 0){
			//write(sd,&tmp,sizeof(tmp)); 
			send(sd,&tmp,sizeof(tmp), 0);

			if (tmp=='\n'){
				//preparo la selct
			       	FD_SET(sd, &read_mask);
						
				//la select ha un tempo pari a zero cosi' e' instantanea
				select(nfds, &read_mask, NULL, NULL, &time);
				printf("select result: %d\n", FD_ISSET(sd, &read_mask));

				//vedo se posso leggere qualcosa
				if (FD_ISSET(sd, &read_mask)){
					letti=recv(sd, buff, sizeof(buff), 0);
					printf("\nHo ricevuto questa linea: %s\n", buff);
					write(fd2, buff, letti);
				}
			}
		}
		//alzo il tempo della select cosi' attende di piu' se il server ci mette un po'
		time.tv_sec=3;

		FD_SET(sd, &read_mask);
		select(nfds, &read_mask, NULL, NULL, &time);
				
		printf("select result: %d\n", FD_ISSET(sd, &read_mask));
		//vedo se posso leggere qualcosa
		while (FD_ISSET(sd, &read_mask)){
			letti=recv(sd, buff, sizeof(buff), 0);
			printf("\nHo ricevuto questa linea: %s\n", buff);
			write(fd2, buff, letti);

	 		FD_SET(sd, &read_mask);
					
			select(nfds, &read_mask, NULL, NULL, &time);	
	 		printf("select result: %d\n", FD_ISSET(sd, &read_mask));
		}		
		close(fd2);
		close(fd);
		
		//salvo le modifiche effettuate con una rinominazione del file
		if (rename(tmpFileName, nomeFile)==0){
			printf("File aggiornato con successo\n");
		}
		else {
			printf("Il file NON e' stato aggiornato\n");
		}
		
		printf("\nInserisci nomeFile\n");	
	
	}
	shutdown(sd,1); //chiusura output, invio EOF al server
	shutdown(sd,0);
	close(sd);

	return 0;
}
