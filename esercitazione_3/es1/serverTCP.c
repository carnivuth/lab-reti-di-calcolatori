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


int main(int argc, char* argv[]){ //args: port
	
	struct hostent *clienthost;
	struct sockaddr_in clientaddr, serveraddr;

	//controllo argomenti
	if (argc != 2){
		perror("Argomenti invalidi\n");
		exit(1);
	}
	int port = atoi(argv[1]);
	if (port < 1024 || port > 65535){
		perror("Porta invalida\n");
		exit(2);
	}
	
	//inizializzazione server
	memset((char*)&serveraddr, 0, sizeof(serveraddr));
	serveraddr.sin_family = AF_INET;
	serveraddr.sin_addr.s_addr = INADDR_ANY;
	serveraddr.sin_port = htons(port);

	//creazione e settaggio socket ascolto
	int listen_sd = socket(AF_INET, SOCK_STREAM, 0);
	if (listen_sd < 0){
		perror("Errore creazione socket\n");
		exit(2);
	}
	int on = 1;
	if (setsockopt(listen_sd, SOL_SOCKET, SO_REUSEADDR, &on, sizeof(on)) < 0){
		perror("Errore di settaggio\n");
		exit(3);
	}
	if (bind(listen_sd, (struct sockaddr *)&serveraddr, sizeof(serveraddr)) < 0){
		perror("Errore di binding\n");
		exit(4);
	}
	if (listen(listen_sd, 5) < 0 ){
		perror("Errore creazione coda listen\n");
		exit(5);
	}
	

	char buff[256];
	char nomeFile[256];
	int riga, dim, pid;
	int curPort;
	char curIp[256];
	int len = sizeof(clientaddr); //va bene fuori? cambianto i client può cambiare la dimensione?
	int conn_sd;
	char tmp;
	int lineCounter=0, charCounter=0;
	while(1){

		//arrivano: nomeFile, riga, dim, contenutoFile
		if ((conn_sd=accept(listen_sd, (struct sockaddr*)&clientaddr, &len))<0){
			if (errno==EINTR){
				perror("Forzo la continuaizone della accept\n");
				continue;
			}else{
				perror("Errore accept\n");
				exit(6);
			}
		}

		if(fork()==0){
			//figlio
			clienthost = gethostbyaddr((char*)&clientaddr.sin_addr, sizeof(clientaddr.sin_addr), AF_INET);
			if (clienthost == NULL){
				printf("Client host information not found\n");
				continue;
			}else{
				printf("Client servito: %s\n", clienthost->h_name);
			}

			close(0);
			dup(conn_sd); //ridirezione input verso il client

			scanf("%s%d%d",buff,riga,dim); //ipotizzo che ci siano dei \0 e che bastino alla scanf per fermarsi
			printf("nomeFile %s riga: %d dim: %d\n");
			while((tmp = getchar()) != EOF){
				printf("letto: %c", tmp);
				if (tmp == '\n'){
					lineCounter++;
					if(lineCounter != riga){
						write(conn_sd, buff, sizeof(char)*charCounter);
					}
					charCounter=0;
				}else{
					buff[charCounter] = tmp;
					charCounter++;
				}
				
			}
			shutdown(conn_sd,1); //mando EOF al client
			shutdown(conn_sd,0);
			close(conn_sd);
			exit(0); //figlio termina

			
		}





	}
		
	return 0;
}
