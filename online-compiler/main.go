package main

import (
	"encoding/base64"
	"errors"
	"fmt"
	"github.com/gin-gonic/gin"
	"log"
	"math/rand"
	"net/http"
	"os"
	"os/exec"
	"path/filepath"
	"strings"
)

func main() {
	var filepath, err = filepath.Abs("./")

	if err != nil {
		panic(err)
	}

	router := gin.Default()
	router.GET("/", func(c *gin.Context) {
		c.String(http.StatusOK, "C-- Online Compiler")
	})

	router.POST("/compile", func(c *gin.Context) {
		var raw, err = c.GetRawData()

		if err != nil {
			log.Println("Gin Context Body (c.getRawData()): Error ", err)
			c.String(http.StatusInternalServerError, "ERR_INTERNAL_ERROR")
			return
		} else {
			if _, err := os.Stat("./tmp"); errors.Is(err, os.ErrNotExist) {
				os.Mkdir("./tmp", 0777)
			}

			var Savepath = filepath + "/tmp/" + fmt.Sprint(RandomInteger()) + ".cpp"
			var decoded, err = base64.RawStdEncoding.DecodeString(string(raw))

			if err != nil {
				log.Println("Base64 Decoding: Error: ", err)
				c.String(http.StatusInternalServerError, "ERR_INTERNAL_ERROR")
				return
			}

			var code = []byte(ReverseCode(string(decoded)))

			if err != nil {
				log.Println("Base64 Decoding: Error: ", err)
				c.String(http.StatusInternalServerError, "ERR_INTERNAL_ERROR")
				return
			}

			log.Println("x86_64-w64-mingw32-g++", "-o", (strings.Split(Savepath, ".")[len(strings.Split(Savepath, "."))-2] + ".exe"), Savepath, " -m64")
			os.WriteFile(Savepath, code, 0777)
			_, err = exec.Command("x86_64-w64-mingw32-g++", "-o", (strings.Split(Savepath, ".")[len(strings.Split(Savepath, "."))-2] + ".exe"), Savepath, "-m64").Output()
			if err != nil {
				switch err.(type) {
				case *exec.Error:
					log.Println("Exec: Error:", err)
					c.String(http.StatusInternalServerError, "ERR_INTERNAL_ERROR")
				case *exec.ExitError:
					log.Println("Exec: Exit Error:", err)
					c.String(http.StatusBadRequest, "ERR_COMPILE_ERROR")
				default:
					log.Println("Exec: Unknown Error:", err)
					c.String(http.StatusInternalServerError, "ERR_INTERNAL_ERROR")
					panic(err)
				}
				return
			} else {
				var bytes, err = os.ReadFile(strings.Split(Savepath, ".")[0] + ".exe")
				if err != nil {
					log.Println("Reading: Error reading file: ", err)
					c.String(http.StatusInternalServerError, "ERR_INTERNAL_ERROR")
					return
				} else {
					var encoded = base64.StdEncoding.EncodeToString(bytes)

					var err1 = os.Remove(Savepath)
					var err2 = os.Remove(strings.Split(Savepath, ".")[0] + ".exe")
					if err1 != nil || err2 != nil {
						fmt.Println("Deleting: Error deleting files", err1, "\n\n", err2)
						c.String(http.StatusInternalServerError, "ERR_INTERNAL_ERROR")
					}

					c.String(http.StatusOK, encoded)
					return
				}
			}

		}
	})

	router.Run("localhost:4444")
}

func RandomInteger() int {
	return rand.Intn(999999)
}

func ReverseCode(c string) string {
	rs := []rune(c)
	for i, j := 0, len(rs)-1; i < j; i, j = i+1, j-1 {
		rs[i], rs[j] = rs[j], rs[i]
	}
	return string(rs)
}
