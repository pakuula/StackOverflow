package c_out

/*
#include <stdlib.h>
#include <stdio.h>

FILE* file = NULL;
char * buf = NULL;

const size_t BSIZE = 64 * 1024;

const int CREATE_FILE     = 0;
const int OPEN_DEV_FILE   = 1;
const int USE_STD_STREAM  = 2;

void CleanUp() {
    if (file != NULL) {
        if (file != stdout && file != stderr) {
            fclose(file);
        }
    }
    if (buf != NULL) {
        free(buf);
    }
}

int PrepareOut(int variant) {
    switch (variant) {
        case CREATE_FILE:
            file = fopen("c_out.txt", "w+b");
            break;
        case OPEN_DEV_FILE:
            file = fopen("/dev/stdout", "a+b");
            break;
        case USE_STD_STREAM:
            file = stdout;
            break;
    }
    if (file == NULL) {
        return 0;
    }
    buf = malloc(BSIZE);
    if (buf == NULL) {
        CleanUp();
        return 0;
    }

    if (setvbuf(file, buf, _IOFBF, BSIZE) != 0) {
        CleanUp();
        return 0;
    }

    return 1;
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

var (
	CreateFile   = int(C.CREATE_FILE)
	OpenDevFile  = int(C.OPEN_DEV_FILE)
	UseStdStream = int(C.USE_STD_STREAM)
	BufSize      = int(C.BSIZE)
)

func Prepare(variant int) bool {
	return C.PrepareOut(C.int(variant)) != 0
}

func Run(n int) {
	C.Run(C.int(n))
}

func CleanUp() {
	C.CleanUp()
}
