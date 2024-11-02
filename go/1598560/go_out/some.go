package go_out

import (
	"bufio"
	"os"
)

var file *os.File

func Prepare() bool {
	f, err := os.Create("go_out.txt")
	if err != nil {
		return false
	}
	file = f
	return true
}

func CleanUp() {
	file.Close()
}

const LoremIpsum = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.\n"

func Run(n int) {
	out := bufio.NewWriter(file)
	for i := 0; i < n; i++ {
		out.WriteString(LoremIpsum)
	}
	out.Flush()
}
