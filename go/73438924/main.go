package main

import (
	"embed"
	"html/template"
	"log"
	"os"

	"github.com/gin-contrib/multitemplate"
	"github.com/gin-gonic/gin"
)

//go:embed pjt_1/*
var f embed.FS

func loadTemplates() multitemplate.Render {
	r := multitemplate.New()

	// Generate our templates map from our layouts/ and includes/ directories

	site, err := embed.FS.ReadDir(f, "pjt_1/templates/site")
	if err != nil {
		panic(err.Error())
	}

	for _, layout := range site {
		embeddedTemplate, err := template.ParseFS(f, "pjt_1/templates/site/"+layout.Name(), "pjt_1/templates/include/"+layout.Name())
		if err != nil {
			log.Println(err)
		}
		r.Add(layout.Name(), embeddedTemplate)
		log.Println(layout.Name() + " loaded")
		embeddedTemplate.Execute(os.Stdout, "")
	}

	return r
}

func main() {
	router := gin.Default()
	router.HTMLRender = loadTemplates()
	router.GET("/", func(c *gin.Context) {
		c.HTML(200, "a.html", gin.H{
			"title": "Html5 Template Engine",
		})
	})
	router.GET("/article", func(c *gin.Context) {
		c.HTML(200, "b.html", gin.H{
			"title": "Html5 Article Engine",
		})
	})
	router.Run(":8080")
}
