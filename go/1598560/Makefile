all: run_volodarsky run_bench

run_volodarsky:
	make -C volodarsky

run_bench:
	go test -benchmem -bench '.' -benchtime=20x ./bench | grep -v Lorem
