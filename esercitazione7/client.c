#include <stdio.h>
#include <rpc/rpc.h>
#include "operazioni.h"

int main(int argc, char *argv[])
{
    // struct chiamate remote
    char *contafile= malloc(256);
    Dir_scan dirscan;
    
    char filename[256];
    Res *resconta;
    int *ris;

    char *server;
    int procedure;
    int dimfile;
    char *nomefile;
    char *nomedir;
    CLIENT *cl;

    if (argc != 2) // controllo argomenti
    {
        fprintf(stderr, "uso:%s host\n", argv[0]);
        exit(1);
    }

    server = argv[1];
    // creazione gestore di trasporto
    cl = clnt_create(server, FILEPROG,
                     FILEVERS, "udp");
    if (cl == NULL)
    {
        clnt_pcreateerror(server);
        exit(1);
    }

    printf("Inserisci procedura\n");

    while (scanf("%d", &procedure) > 0)
    {

        if (procedure == 1)
        {
            printf("Inserisci il nome del file\n");
            scanf("%s", contafile);
            resconta = contafile_1(&contafile, cl);
            if (resconta == NULL)
            {
                clnt_perror(cl, server);
                exit(1);
            }
            if (&(resconta->first) == NULL || &(resconta->second) == NULL || &(resconta->third) == NULL)
            {
                clnt_perror(cl, server);
                exit(1);
            }
            if (resconta->first == -1 || resconta->second == -1 || resconta->third == -1)
            {
                printf("file %s non esistente\n",contafile);
                
            }else{
            
                printf("Inseriti i valori: %d-%d-%d\n", resconta->first, resconta->second, resconta->third);
            }
                
        }
        else if (procedure == 2)
        {

            printf("Inserisci il nome del direttorio\n");
            scanf("%s",filename);
            printf("Inserisci la dimensione minima\n");
            while(scanf("%i", &((&(dirscan))->dimfile))<=0){
                gets();
                printf("Inserisci la dimensione minima\n");
            }
           
            
            
            dirscan.filename=filename;
            
            ris = contadir_1(&dirscan, cl);
            if (ris == NULL)
            {
                clnt_perror(cl, server);
                exit(1);
            }
            if(*ris==-1){
                printf("cartella %s non esistente\n",filename);
                
            }else{
                printf("risultato:%d\n",*ris);
            }
        }

        printf("Inserisci procedura\n");
    }

    
    // libero la risorsa gestore di trasporto
    clnt_destroy(cl);
}
