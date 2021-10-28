#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <signal.h>
#include <errno.h>
#include <fcntl.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <string.h>
#define LINE_LENGTH 256

int main(int argc, char **argv){
	int sd, port, len, result=0, i=0;
    const int on = 1;
	struct sockaddr_in cliaddr, servaddr;
	struct hostent *clienthost;
    char fileName[LINE_LENGTH];
    int fd;
    char buf;

	/* CONTROLLO ARGOMENTI ---------------------------------- */
	if(argc!=2){
		printf("Error: %s port\n", argv[0]);
		exit(1);
	}
	else{
		while( argv[1][i]!= '\0' ){
			if((argv[1][i] < '0') || (argv[1][i] > '9')){
				printf("Secondo argomento non intero\n");
				printf("Error: %s port\n", argv[0]);
				exit(2);
			}
			i++;
		}  	
	  	port = atoi(argv[1]);
  		if (port < 1024 || port > 65535){
		      printf("Error: %s port\n", argv[0]);
		      printf("1024 <= port <= 65535\n");
		      exit(2);  	
  		}
	}

	/* INIZIALIZZAZIONE INDIRIZZO SERVER ---------------------------------- */
	memset ((char *)&servaddr, 0, sizeof(servaddr));
	servaddr.sin_family = AF_INET;
	servaddr.sin_addr.s_addr = INADDR_ANY;  
	servaddr.sin_port = htons(port);  

	/* CREAZIONE, SETAGGIO OPZIONI E CONNESSIONE SOCKET -------------------- */
	sd=socket(AF_INET, SOCK_DGRAM, 0);
	if(sd <0){perror("creazione socket "); exit(1);}
	printf("Server: creata la socket, sd=%d\n", sd);

	if(setsockopt(sd, SOL_SOCKET, SO_REUSEADDR, &on, sizeof(on))<0)
	{perror("set opzioni socket "); exit(1);}
	printf("Server: set opzioni socket ok\n");

	if(bind(sd,(struct sockaddr *) &servaddr, sizeof(servaddr))<0)
	{perror("bind socket "); exit(1);}
	printf("Server: bind socket ok\n");

    //corpo del server
    for(;;){
        len=sizeof(struct sockaddr_in);
        //ricevo il nome del file
        if (recvfrom(sd, fileName, sizeof(fileName), 0, (struct sockaddr *)&cliaddr, &len)<0){
            perror("recvfrom \n"); 
            continue;
        }

        //verifico l'esistenza del file
        if((fd=open(fileName, O_RDONLY))<0){
            printf("Il file %s non esiste\n", fileName);
            result=-1;
        }
        else{
            int tmp=0;
            //leggo ogni riga e poi cerco la parola più lunga
            while(read(fd, &buf, 1)>0){
                if (buf==' ' || buf=='\n'){
                    if (result<tmp) {
                        result=tmp;
                        tmp=0;
                    }
                }
                else tmp++;
            }
        }
        printf("Il file %s esiste, il risultato e' %d\n", fileName, result);

        //invio risposta
        if (sendto(sd, &result, sizeof(result), 0, (struct sockaddr *)&cliaddr, len)<0){
            perror("sendto \n"); 
            continue;
        }
    }

}