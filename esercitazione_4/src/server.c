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

#include <arpa/inet.h>

#include <dirent.h>

#include <sys/stat.h>

#define LINE_LENGTH 256

int main(int argc, char * argv[]) { //args: port

    //controllo argomenti
    if (argc != 2) {
        perror("Argomenti invalidi\n");
        exit(1);
    }
    int port = atoi(argv[1]);
    if (port < 1024 || port > 65535) {
        perror("Porta invalida\n");
        exit(2);
    }

    struct sockaddr_in serveraddr, clientaddr, cliaddr;
    int len = sizeof(clientaddr);

    //inizializzazione server
    memset((char * ) & serveraddr, 0, sizeof(serveraddr));
    serveraddr.sin_family = AF_INET;
    serveraddr.sin_addr.s_addr = INADDR_ANY;
    serveraddr.sin_port = htons(port);

    int on = 1; //serve come parametro delle opzioni delle socket
    //creazione e settaggio socket TCP
    int listen_sd = socket(AF_INET, SOCK_STREAM, 0);
    if (listen_sd < 0) {
        perror("Errore creazione socket TCP\n");
        exit(2);
    }
    if (setsockopt(listen_sd, SOL_SOCKET, SO_REUSEADDR, & on, sizeof(on)) < 0) {
        perror("Errore di settaggio\n");
    }

    if (bind(listen_sd, (struct sockaddr * ) & serveraddr, sizeof(serveraddr)) < 0) {
        perror("Errore di binding\n");
        exit(3);
    }
    if (listen(listen_sd, 5) < 0) {
        perror("Errore di listen\n");
        exit(4);
    }

    //creazione e settaggio socket UDP
    int udp_sd = socket(AF_INET, SOCK_DGRAM, 0);
    if (udp_sd < 0) {
        perror("Errore di creazione socket UDP\n");
        exit(5);
    }
    if (setsockopt(udp_sd, SOL_SOCKET, SO_REUSEADDR, & on, sizeof(on)) < 0) {
        perror("Errore di settaggio socket UDP\n");
        exit(6);
    }
    if (bind(udp_sd, (struct sockaddr * ) & serveraddr, sizeof(serveraddr)) < 0) {
        perror("Errore di binding UDP\n");
        exit(7);
    }

    printf("server located on %s:%d\n", inet_ntoa(serveraddr.sin_addr), ntohs(serveraddr.sin_port));

    //preparazione maschera
    fd_set fdset;
    int fdsn = ((listen_sd >= udp_sd) ? listen_sd : udp_sd) + 1;
    FD_ZERO( & fdset);

    //variabili usate nel loop
    int nready; //valore di ritorno della select
    int conn_sd; //file descriptor della connessione stream
    //variabili connessione datagram
    int result;
    char fileName[LINE_LENGTH], parola[LINE_LENGTH];
    int fd, fd2; //file che leggo e file temporaneo

    //main loop
    for (;;) {

        //metto la maschera in ascolto sulle due socket
        FD_SET(listen_sd, & fdset);
        FD_SET(udp_sd, & fdset);

        //uso la select per vedere se qualcuno e' pronto
        if ((nready = select(fdsn, & fdset, NULL, NULL, NULL)) < 0) {
            if (errno == EINTR) {
                perror("Interruzione da segnale, si riparte\n");
                continue;
            } else {
                perror("Errore nella select\n");
                exit(8);
            }
        }

        //gestisco la socket stream
        if (FD_ISSET(listen_sd, & fdset)) {

            printf("Servo TCP\n");
            //creazione figlio
            conn_sd = accept(listen_sd, (struct sockaddr * ) & clientaddr, & len);
            if (fork() == 0) {
                //figlio
                printf("fork started\n");

                //variabili che userà il figlio
                int res = 0;
                char nomeDir[LINE_LENGTH];
                char dirPath[LINE_LENGTH]; //qui costruiro' i path delle sotto-directory
                DIR * d, * innerD; //directory
                struct dirent * dir, * innerDir; //file nelle directory
                struct stat stats; //struttura in cui salvero' le informazioni sui file

                //continuo l'esecuzione fin quando ricevo il nome del file
                while (read(conn_sd, nomeDir, sizeof(nomeDir)) > 0) {
                    printf("\nNome Directory: %s\n", nomeDir);

                    //verifico esistenza directory
                    d = opendir(nomeDir);
                    if (d == NULL) {
                        perror("Impossibile aprire la directory\n");
                        //notifica errore apertura
                        res = -1;
                        write(conn_sd, & res, sizeof(int));
                        continue;
                    }
                    //notifico l'apertura della directory
                    res = 1;
                    write(conn_sd, & res, sizeof(int));

                    //scorro le sotto-directory
                    while ((dir = readdir(d)) != NULL) {
                        //printf("%s\n", dir -> d_name);
                        if (strcmp(dir -> d_name, ".") == 0 || strcmp(dir -> d_name, "..") == 0) {
                            //printf("cartella . o .. da saltare\n");
                            continue;
                        }

                        //costruzione della sotto-directory
                        strcpy(dirPath, nomeDir);
                        strcat(dirPath, "/");
                        strcat(dirPath, dir -> d_name);
                        printf("sotto-directory: %s\n", dirPath);
                        stat(dirPath, & stats);
                        if (S_ISDIR(stats.st_mode)) {
                            //apro la sotto-directory
                            innerD = opendir(dirPath);
                            //ciclo finche' leggo file
                            while ((innerDir = readdir(innerD)) != NULL) {
                                if (strcmp(innerDir -> d_name, ".") && strcmp(innerDir -> d_name, "..")) {
                                    //printf("found: %s\n", innerDir -> d_name);
                                    write(conn_sd, innerDir -> d_name, 256);
                                }
                                //else printf("Cartella da saltare\n");

                            }

                        }

                    }

                }
                //chiudo la socket
                close(conn_sd);

                //terminazione figlio
                exit(0);

            }
        }

        //gestisco la socket datagram
        if (FD_ISSET(udp_sd, & fdset)) {
            result = 0;

            printf("Servo UDP\n");
            len = sizeof(struct sockaddr_in);
            //ricevo il nome del file e parola da cercare
            if (recvfrom(udp_sd, fileName, sizeof(fileName), 0, (struct sockaddr * ) & cliaddr, & len) < 0) {
                perror("recvfrom filename \n");
                continue;
            }
            if (recvfrom(udp_sd, parola, sizeof(parola), 0, (struct sockaddr * ) & cliaddr, & len) < 0) {
                perror("recvfrom parola \n");
                continue;
            }

            printf("Ho ricevuto filename: %s e parola: %s\n", fileName, parola);

            //verifico l'esistenza del file
            if ((fd = open(fileName, O_RDONLY)) < 0) {
                printf("Il file %s non esiste\n", fileName);
                result = -1;
            }
            //creo il file temporaneo
            if ((fd2 = open("tmp.txt", O_WRONLY | O_CREAT | O_TRUNC, 0666)) < 0) {
                printf("Errore creazione file temporaneo");
                result = -1;
            }

            if (result == 0) {

                char c;
                char buf[LINE_LENGTH];
                int i = 0;

                //leggo il file carattere per carattere
                while (read(fd, & c, 1) > 0) {
                    if (c == ' ' || c == '\n') {

                        buf[i] = '\0';
                        if (strcmp(parola, buf)) {
                            //parola diversa strcmp=1
                            //inserisco il terminatore nel buf e lo scrivo
                            buf[i] = c;
                            write(fd2, buf, i + 1);
                            i = 0;
                        } else {
                            //caso in cui la parola sia stata trovata strcmp=0
                            result++;
                            i = 0;
                            //scrivo solo il terminatore
                            write(fd2, & c, 1);
                        }
                    } else {
                        //se non incontro il terminatore salvo il carattere letto nel buffer
                        buf[i] = c;
                        i++;
                    }

                }

                //controllo cio' che e' rimasto nel buffer
                buf[i] = '\0';
                if (strcmp(parola, buf)) {
                    //parola diversa strcmp=1
                    write(fd2, buf, i);
                    i = 0;
                } else {
                    result++;
                    i = 0;
                }

            }
            printf("Il file %s esiste, il risultato e' %d\n", fileName, result);

            //invio risposta
            if (sendto(udp_sd, & result, sizeof(result), 0, (struct sockaddr * ) & cliaddr, len) < 0) {
                perror("sendto \n");
                continue;
            }
            //chiudo i file
            close(fd);
            close(fd2);

            //salvo le modifiche effettuate con una rinominazione del file
            if (rename("tmp.txt", fileName) == 0) {
                printf("File aggiornato con successo\n");
            } else {
                printf("Il file NON e' stato aggiornato\n");
            }

        }

    }

}
