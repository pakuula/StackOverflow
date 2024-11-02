package bench_test

import (
	"some/c_out"
	"some/go_out"
	"testing"
)

const N_LINES = 10000

func BenchmarkGoToFile(b *testing.B) {
	for i := 0; i < b.N; i++ {
		if !go_out.Prepare(c_out.CreateFile) {
			b.Fatal("failed to open file")
		}
		go_out.Run(N_LINES)
		go_out.CleanUp()
	}
}

func BenchmarkC_ToFile(b *testing.B) {
	for i := 0; i < b.N; i++ {
		if !c_out.Prepare(c_out.CreateFile) {
			b.Fatal("failed to open file")
		}
		c_out.Run(N_LINES)
		c_out.CleanUp()
	}
}

func BenchmarkGoToDevFile(b *testing.B) {
	for i := 0; i < b.N; i++ {
		if !go_out.Prepare(c_out.OpenDevFile) {
			b.Fatal("failed to open file")
		}
		go_out.Run(N_LINES)
		go_out.CleanUp()
	}
}
func BenchmarkC_ToDevFile(b *testing.B) {
	for i := 0; i < b.N; i++ {
		if !c_out.Prepare(c_out.OpenDevFile) {
			b.Fatal("failed to open file")
		}
		c_out.Run(N_LINES)
		c_out.CleanUp()
	}
}

func BenchmarkGoToStdStream(b *testing.B) {
	for i := 0; i < b.N; i++ {
		if !go_out.Prepare(c_out.UseStdStream) {
			b.Fatal("failed to open file")
		}
		go_out.Run(N_LINES)
		go_out.CleanUp()
	}
}
func BenchmarkC_ToStdStream(b *testing.B) {
	for i := 0; i < b.N; i++ {
		if !c_out.Prepare(c_out.UseStdStream) {
			b.Fatal("failed to open file")
		}
		c_out.Run(N_LINES)
		c_out.CleanUp()
	}
}
