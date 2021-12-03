#include <stdio.h>
#include <rpc/rpc.h>
#include "operazioni.h"

Res *contafile_1_svc(char **nomefile, struct svc_req *rp)
{
    static Res* res;
    printf("Operandi ricevuti: %s\n", nomefile);
    res->first=1;
    res->second=1;
    res->third=1;
    // printf("Somma: %i\n", ris);
    return (res);
}

int *contadir_1_svc(Dir_scan * dir, struct svc_req *rp)
{
    static int ris;
    ris=2;
    printf("Operandi ricevuti: %d e %s\n", dir->dimfile, dir->filename);
    // ris = (op->op1) * (op->op2);
    // printf("Moltiplicazione: %i\n", ris);
    return (&ris);
}
