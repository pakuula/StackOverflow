package c_out

/*
#include <stdlib.h>
#include <stdio.h>

FILE* file = NULL;
int PrepareOut() {
    // file = tmpfile();
    file = fopen("c_out.txt", "w+b");
    // file = stdout;
    return (file != NULL);
}

void CleanUp() {
    fclose(file);
}

const char LoremIpsum[] = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.\n";

void Run(int n) {
    for (int i = 0; i < n; i++) {
        fputs(LoremIpsum, file);
    }
    fflush(file);
}

*/
import "C"

func Prepare() bool {
	return C.PrepareOut() != 0
}

func Run(n int) {
	C.Run(C.int(n))
}

func CleanUp() {
	C.CleanUp()
}
