package db_driver

import (
	"database/sql"
	"fmt"
	"log"
	"os"

	_ "github.com/go-sql-driver/mysql"
	"github.com/joho/godotenv"
)

type News struct {
	ID         int64
	Title      string
	Content    string
	Categories []int64
}

type Categories struct {
	ID      int64
	News_ID int64
}

// init is invoked before main()
var db *sql.DB

func init() {

	godotenv.Load()

	user := os.Getenv("DB_USER")
	password := os.Getenv("DB_PASSWORD")

	var err error
	db, err = sql.Open("mysql", fmt.Sprintf("%s:%s@/reform", user, password))
	if err != nil {
		log.Fatal(err)
	}

}

func GetAllNews() ([]News, error) {
	var news_list []News

	rows, err := db.Query("SELECT News.Id, News.Title, News.Content, (SELECT GROUP_CONCAT(NC.CategoryId) FROM NewsCategories NC WHERE NC.NewsId = News.Id) as Categories FROM News;")
	if err != nil {
		return nil, fmt.Errorf("get_list_news: %v", err)
	}
	defer rows.Close()
	for rows.Next() {
		var news News
		if err := rows.Scan(&news.ID, &news.Title, &news.Content, &news.Categories); err != nil {
			return nil, fmt.Errorf("get_list_news: %v", err)
		}
		news_list = append(news_list, news)
	}
	if err := rows.Err(); err != nil {
		return nil, fmt.Errorf("get_list_news: %v", err)
	}
	return news_list, nil
}

func PushOne(news News) {
	result, err := db.Exec("INSERT INTO News (Title, Content) VALUES (?, ?)", news.Title, news.Content)
	if err != nil {
		fmt.Println("get_list_news: %v", err)
	}
	id, err := result.LastInsertId()
	if err != nil {
		fmt.Println("get_list_news: %v", err)
	}
	list_categories := news.Categories
	for _, value := range list_categories {
		_, err := db.Exec("INSERT INTO NewsCategories (NewsId, CategoryId) VALUES (?, ?)", id, value)
		if err != nil {
			fmt.Println("get_list_news: %v", err)
		}
	}
}
