#include <stdio.h>
#include <rpc/rpc.h>
#include "fattore.h"
#include <string.h>

int main(int argc, char *argv[])
{
    CLIENT *cl;

    if (argc != 2)
    {
        perror("Argomenti invalidi\n");
        exit(1);
    }
    char *server = argv[1];
    if((cl=clnt_create(server, esercitazioneNove, 1, "tcp"))==NULL){
        clnt_pcreateerror(server);
        exit(1);
    }

    char mode[10], op[10], nome[30];
    Output *output;
    input_esprimi_voto input;

    printf("Classifica (c) o esprimi voto (e)\n");
    while (scanf("%s", mode) == 1)
    {
        if (strcmp(mode, "c") == 0)
        {
            output = classifica_giudici_1((void *)NULL, cl);
            if (output == NULL)
            {
                clnt_perror(cl, server);
                exit(1);
            }

            for (int i = 0; i < 256; i++)
            {

                if (output->giudici[i].nome == NULL)
                {
                    clnt_perror(cl, server);
                    exit(1);
                }
            }
            for (int i = 0; i < 256; i++)
            {
                if (strcmp(output->giudici[i].nome, "NULL") != 0)
                {
                    printf("Giudice: %s\n", output->giudici[i].nome);
                }
            }
        }
        else if (strcmp(mode, "e") == 0)
        {
            printf("Aggiungere (a) o sottrarre (s)\n");
            scanf("%s", op);
            if (op != NULL && (strcmp(op, "a") == 0 || strcmp(op, "s") == 0))
            {
                printf("Inserisci candidato\n");
                scanf("%s", nome);
                strcpy(input.candidato, nome);
                strcpy(input.op, op);
                esprimi_voto_1(&input, cl);
            }
        }
        else
        {
            printf("Operazione invalida\n");
        }
        printf("Classifica (c) o esprimi voto (e)\n");
    }

    clnt_destroy(cl);
}