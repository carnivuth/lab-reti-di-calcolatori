
int N-ARG,IND
int main(int argc char* argv[]){
	//controllo numero 
	if(argc!=N-ARG){
		exit(1);
	}
	//controllo carattere
	if(argv[IND]<65 || argv[IND]>90 && argv[IND]<97 || argv[IND]>122){
		exit(1);
	}
	//controllo cifra
	if(argv[IND]<48 || argv[IND]>57 ){
		exit(1);
	}
	
}