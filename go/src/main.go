package main

import (
	"fmt"
	"os"
	"strings"
)

func ReadFile(fileName string) string {
	data, error := os.ReadFile(fileName)
	if error != nil {
		panic(error)
	}
	return string(data)
}

func parseLine(line string) int {
	if len(line) == 0 {
		return 0
	}

	firstNumber := -1
	lastNumber := 0
	for _, char := range line {
		if char >= '0' && char <= '9' {
			digit := int(char - '0')
			if firstNumber == -1 {
				firstNumber = digit
			}
			lastNumber = digit
		}
	}
	return firstNumber * 10 + lastNumber
}

var alphaNumbers = map[string]int{
	"one": 1,
	"two": 2,
	"three": 3,
	"four": 4,
	"five": 5,
	"six": 6,
	"seven": 7,
	"eight": 8,
	"nine": 9,
}

func replaceAt(line string, char rune, index int) string {
	return line[:index] + string(char) + line[index + 1:]
}

func replaceAlphaNumbers(line string) string {
	result := ""
	for _, char := range line {
		if char >= '0' && char <= '9' {
			result = result + string(char)
		} else {
			result += "_"
		}
	}
	for alpha, number := range alphaNumbers {
		for index, _ := range line {
			alphaIndex := strings.Index(line[index:], alpha)
			if alphaIndex > -1 {
				result = replaceAt(result, rune(number + '0'), alphaIndex + index)
			}
		}
	}
	return result
}

func main() {
	inputData := ReadFile("../data/2023/day1/input.txt")
	lines := strings.Split(inputData, "\n")
	numberSum := 0
	alphaNumberSum := 0
	for _, line := range lines {
		numberSum += parseLine(line)
		alphaNumberSum += parseLine(replaceAlphaNumbers(line))
	}
	fmt.Println(numberSum, alphaNumberSum)
}
