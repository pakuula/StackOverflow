package api

import (
	"log"
	"my-project/db_driver"

	"github.com/gofiber/fiber/v2"
)

func Api() {
	app := fiber.New()

	app.Get("/list", func(c *fiber.Ctx) error {
		_, err := db_driver.GetAllNews()
		return err
	})

	app.Get("/:param", func(c *fiber.Ctx) error {
		return c.SendString("param: " + c.Params("param"))
	})

	app.Post("/", func(c *fiber.Ctx) error {
		return c.SendString("POST request")
	})

	log.Fatal(app.Listen(":3001"))

}
