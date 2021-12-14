/*
 * Please do not edit this file.
 * It was generated using rpcgen.
 */

#ifndef _FATTORE_H_RPCGEN
#define _FATTORE_H_RPCGEN

#include <rpc/rpc.h>


#ifdef __cplusplus
extern "C" {
#endif


struct giudice {
	char nome[256];
};
typedef struct giudice giudice;

struct Output {
	struct giudice giudici[256];
};
typedef struct Output Output;

struct input_esprimi_voto {
	char candidato[256];
	char op[2];
};
typedef struct input_esprimi_voto input_esprimi_voto;

#define esercitazioneNove 0x20000022
#define SCANVERS 1

#if defined(__STDC__) || defined(__cplusplus)
#define classifica_giudici 1
extern  Output * classifica_giudici_1(void *, CLIENT *);
extern  Output * classifica_giudici_1_svc(void *, struct svc_req *);
#define esprimi_voto 2
extern  void * esprimi_voto_1(input_esprimi_voto *, CLIENT *);
extern  void * esprimi_voto_1_svc(input_esprimi_voto *, struct svc_req *);
extern int esercitazionenove_1_freeresult (SVCXPRT *, xdrproc_t, caddr_t);

#else /* K&R C */
#define classifica_giudici 1
extern  Output * classifica_giudici_1();
extern  Output * classifica_giudici_1_svc();
#define esprimi_voto 2
extern  void * esprimi_voto_1();
extern  void * esprimi_voto_1_svc();
extern int esercitazionenove_1_freeresult ();
#endif /* K&R C */

/* the xdr functions */

#if defined(__STDC__) || defined(__cplusplus)
extern  bool_t xdr_giudice (XDR *, giudice*);
extern  bool_t xdr_Output (XDR *, Output*);
extern  bool_t xdr_input_esprimi_voto (XDR *, input_esprimi_voto*);

#else /* K&R C */
extern bool_t xdr_giudice ();
extern bool_t xdr_Output ();
extern bool_t xdr_input_esprimi_voto ();

#endif /* K&R C */

#ifdef __cplusplus
}
#endif

#endif /* !_FATTORE_H_RPCGEN */
