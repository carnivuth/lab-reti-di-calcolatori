#include <stdio.h>

#include <stdlib.h>

#include <unistd.h>

#include <sys/types.h>

#include <sys/socket.h>

#include <netinet/in.h>

#include <netdb.h>

#include <string.h>

#include <fcntl.h>

int main(int argc, char * argv[]) { //args: ipServ portServ

    struct hostent * host;
    struct sockaddr_in serveraddr;
    struct timeval time;

    //metto un secondo di attesa
    time.tv_sec = 1;
    time.tv_usec = 0;

    //creo la maschera
    fd_set read_mask;
    FD_ZERO( & read_mask);

    //controllo argomenti
    if (argc != 3) {
        perror("Argomenti invalidi\n");
        exit(1);
    }

    host = gethostbyname(argv[1]);
    if (host == NULL) {
        perror("Host non presente in /etc/hosts\n");
        exit(2);
    }
    int port = atoi(argv[2]);
    if (port < 1024 || port > 65535) {
        perror("Porta invalida\n");
        exit(3);
    }

    //inizialzzazione client e server	
    memset((char * ) & serveraddr, 0, sizeof(serveraddr));
    serveraddr.sin_family = AF_INET;
    serveraddr.sin_addr.s_addr = ((struct in_addr * )(host -> h_addr)) -> s_addr;
    serveraddr.sin_port = htons(port);

    //creazione socket
    int sd = socket(AF_INET, SOCK_STREAM, 0); //???
    if (sd < 0) {
        perror("Errore di apertura socket\n");
        exit(2);
    }

    //bind socket implicita nella connect
    if (connect(sd, (struct sockaddr * ) & serveraddr, sizeof(struct sockaddr)) < 0) {
        perror("Errore di binding (connect)\n");
        exit(3);
    }

    char nomeFile[256], buff[256];
    int riga, ok, dim, fd, fd2;
    char tmp;
    int len = sizeof(serveraddr);
    int res = 0;
    int nfds = sd + 1;

    printf("Inserisci nome Directory\n");
    while (scanf("%s", nomeFile) != EOF) {

        //invio il nome del file
        if (sendto(sd, nomeFile, sizeof(nomeFile), 0, (struct sockaddr * ) & serveraddr, len) < 0) {
            perror("Errore di trasmissione\n");
            exit(4);
        }

        printf("Inviato nome file %s\n", nomeFile);

        if (recv(sd, & res, sizeof(res), 0) < 0) {
            perror("recv\n");
            continue;
        }
        printf("Esito: %d\n", res);
        int counter = 0;
        char buff[256];
        if (res >= 0) {
            //metto la maschera in ascolto sulla socket
            FD_SET(sd, & read_mask);
            select(nfds, & read_mask, NULL, NULL, & time);
            while (FD_ISSET(sd, & read_mask)) {

                //fai la recv
                if (recv(sd, buff, sizeof(buff), 0) < 0) {
                    perror("recv\n");
                    continue;
                }
                printf("Nome file: %s\n", buff);

                //risetto la mascehra in ascolto sulla socket
                FD_SET(sd, & read_mask);

                select(nfds, & read_mask, NULL, NULL, & time);

            }
            memset((char * ) & buff, 0, sizeof(buff));

        } else {
            printf("Il file non è stato trovato\n");
        }
        printf("\nInserisci nome Directory\n");

    }
    shutdown(sd, 1); //chiusura output, invio EOF al server
    shutdown(sd, 0);

    return 0;
}
