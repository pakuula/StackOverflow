#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <stdbool.h>
#include <getopt.h>

void search(char b[], int n, int i, bool do_print) {
    if (i == n) {
        // char*p = b;
        // char c;
        // int fd = fileno(stdout);
        // write(fd, b, strlen(b));
        
        // fputs(b, stdout);
        if (do_print) {
            fwrite(b, 1, n + 1, stdout);
        }
    } else {
        for (char d = '0'; d <= '9'; ++d) {
            b[i] = d;
            search(b, n, i + 1, do_print);
        }
    }
}

typedef struct _iter_t {
    char* buf;
    int pos;
    int last;
} iter_t;

iter_t * new_iter(size_t len) {
    iter_t * ptr = malloc(sizeof(iter_t));
    if (ptr == NULL) {
        perror("failed to allocate iterator");
        
        return NULL;
    }
    ptr->buf = malloc(len+2);
    if (ptr->buf == NULL) {
        perror("failed to allocate buffer");
        free(ptr);
        return NULL;
    }
    memset(ptr->buf, '0', len);
    ptr->buf[len] = '\n';
    ptr->buf[len+1] = '\0';
    ptr->pos = ptr->last = len-1;
    return ptr;
}

const char * value(const iter_t* ptr) {
    return ptr->buf;
}

const char _next_digit[] = "1234567890";

bool next(iter_t* ptr) {
    int i;
    for (i = ptr->pos; i >= 0; i--) {
        char* p_digit = ptr->buf + i;
        char old = *p_digit; 
        *p_digit = _next_digit[old - '0'];
        if (*p_digit != '0') {
            if (i != ptr->pos) {
                ptr->pos = ptr->last;
            }
            return true;
        }
    }
    return false;
}

const size_t BSIZE = 64*1024;

int main0() {
    char * buf = malloc(BSIZE);
    if (setvbuf(stdout, NULL, _IOFBF, BSIZE) != 0) {
        return 1;
    }
    // flockfile(stdout);
    int n;
    if (scanf("%d", &n) != 1) {
        return 1;
    }

    char *b = malloc((size_t)n + 2);
    b[n] = '\n';
    b[n + 1] = '\0';
    search(b, n, 0, true);
}

const char * opts = "hinlb:";

int main(int argc, char **argv) {
    bool do_print = true;
    bool do_iter = true;
    bool do_lock = false;
    size_t bsize = BSIZE;
    char opt;

    

    while ((opt = getopt(argc, argv, opts)) != -1) {
        if (opt == 'h') {
            fprintf(stderr, "%s [-h] [-n] [-i] [-b SIZE] LEN\n", argv[0]);
            fputs(" -h       print help message\n", stderr);
            fputs(" -n       no print\n", stderr);
            fputs(" -i       use iterator\n", stderr);
            fputs(" -l       use flock\n", stderr);
            fputs(" -b SIZE  set buffer size, defaults to 65536\n", stderr);
            return 0;
        }
        switch(opt) {
            case 'n':
                do_print = false;
                break;
            case 'l':
                do_lock = true;
                break;
            case 'b':
                if (sscanf(optarg, "%lu", &bsize) != 1) {
                    perror("failed to parse buffer size");
                    return 1;
                }
                if (bsize == 0) {
                    bsize = BSIZE;
                }
                break;
            case 's':
                do_iter = false;
                break;
            case '?':
                return 1;
        }
    }
    if (optind == argc) {
        fputs("missing sequence length", stderr);
        return 1;
    }

    // printf("Options: \n");
    // printf(" print: %c\n", (do_print)?'y':'n');
    // printf(" lock: %c\n", (do_lock)?'y':'n');
    // printf(" iter: %c\n", (do_iter)?'y':'n');
    // printf(" buf size: %lu\n", bsize);
    // printf(" length: %s\n", argv[optind]);

    int n;
    if (sscanf(argv[optind], "%d", &n) != 1) {
        return 1;
    }
    if (n<=0) {
        return 2;
    }
    
    char * buf = malloc(bsize);
    if (setvbuf(stdout, NULL, _IOFBF, BSIZE) != 0) {
        return 1;
    }
    if (do_lock) {
        flockfile(stdout);
    }
    
    if (do_iter) {
        iter_t * ptr = new_iter(n);
        if (ptr == NULL) {
            return 3;
        } 
        do {
            if (do_print) {
                fwrite(value(ptr), 1, n+1, stdout);
            }
        } while(next(ptr));
    } else {
        char *b = malloc((size_t)n + 2);
        b[n] = '\n';
        b[n + 1] = '\0';
        search(b, n, 0, do_print);
    }

    return 0;
}