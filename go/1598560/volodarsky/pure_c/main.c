#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>

void search(char b[], int n, int i) {
    if (i == n) {
        // char*p = b;
        // char c;
        // int fd = fileno(stdout);
        // write(fd, b, strlen(b));
        
        fputs(b, stdout);
    } else {
        for (char d = '0'; d <= '9'; ++d) {
            b[i] = d;
            search(b, n, i + 1);
        }
    }
}

int main() {
    if (setvbuf(stdout, NULL, _IOFBF, 64 * 1024) != 0) {
        return 1;
    }

    int n;
    if (scanf("%d", &n) != 1) {
        return 1;
    }

    char *b = malloc((size_t)n + 2);
    b[n] = '\n';
    b[n + 1] = '\0';
    search(b, n, 0);
}