module Main where

import Data.Bits
import System.Environment

data ECPoint = Point (Integer, Integer) | O deriving (Eq, Show)

--instance Eq ECPoint where
--Point a == Point b = a == b
--O == O = True
--_ == _ = False

--exponentMod :: Integer -> Integer -> Integer -> Integer -> Integer
--exponentMod _ _ -1 = 1
--exponentMod base exp modulus x =
--		| set == True = mod ((pow base $ 1 `shift` x) * (exponentMod exp base modulus x-1)) modulus
--		| otherwise = exponentMod base exp mod x-1
--		where set = exp `shift` (-x) .&. 1 == 1

exponentMod :: Integer -> Integer -> Integer -> Integer
exponentMod base exp modulus =
	let exponents = expToBitVector exp modulus
	in exponentMod' base exponents modulus

exponentMod' :: Integer -> [Integer] -> Integer -> Integer
exponentMod' _ [] _ = 1
exponentMod' base (x:xs) modulus =
	let raise = (\z -> (base^z) `mod` modulus)
	in ((raise x) * (exponentMod' base xs modulus)) `mod` modulus

--gets the powers to raise by
expToBitVector :: Integer -> Integer -> [Integer]
expToBitVector exp modulus =
	let 	o = ceiling (logBase (fromIntegral 2) (fromIntegral exp))
		f = (\x -> ((exp `shift` (-x)) .&. 1) == 1)
		raise = (\ex -> (1 `shift` ex))
	in map raise $ filter f [0..o]

multiplicativeInverse :: Integer -> Integer -> Integer
multiplicativeInverse a m =
	let t1 = extendedEuclideanAlgorithm m (a `mod` m) 0 1
	in if t1==0 then 0 else if t1<0 then t1+m else t1

extendedEuclideanAlgorithm :: Integer -> Integer -> Integer -> Integer -> Integer
extendedEuclideanAlgorithm x y t2 t1
	| r == 0 = if y/=1 then 0 else t1
	| otherwise = extendedEuclideanAlgorithm y r t1 t
	where   r = x `mod` y
		q = (x - r) `div` y
		t = t2 - (t1*q)


computeAllSquaresMod :: [Integer] -> Integer -> [(Integer, Integer)]
computeAllSquaresMod elements modulus = map (\x -> (x,x^2 `mod` modulus)) elements

computeAndMatchMod :: [Integer] -> [(Integer, Integer)] -> (Integer -> Integer) -> Integer -> [(Integer, Integer)]
computeAndMatchMod elements squares f modulus = [(x, fst square) |  x <- elements, square <- squares, (f x) `mod` modulus == snd square]

computeEllipticGroupElements :: Integer -> (Integer -> Integer) -> [(Integer, Integer)]
computeEllipticGroupElements modulus curve =
	let 	elements = [0..modulus-1]
		squares = computeAllSquaresMod elements modulus
	in computeAndMatchMod elements squares curve modulus

printSquares :: [Integer] -> Integer -> IO()
printSquares elements mod = let squares = computeAllSquaresMod elements mod
			in putStrLn "y   y**2" >> mapM_ (\(x,y) -> putStrLn $ (show x) ++ "  " ++ (show y)) squares  >> return ()

printEllipticGroupElements :: Integer -> (Integer -> Integer) -> IO()
printEllipticGroupElements modulus curve = let members = computeEllipticGroupElements modulus curve
				     	   in mapM (\(x,y) -> putStrLn $ "(" ++ (show x) ++ "," ++ (show y) ++ ")") members >> return ()

pointMultiplication :: ECPoint -> Integer -> Integer -> Integer -> Integer -> ECPoint
pointMultiplication point multiplyBy a b p = pointMultiplication' point point multiplyBy a b p

pointMultiplication' :: ECPoint -> ECPoint -> Integer -> Integer -> Integer -> Integer -> ECPoint
pointMultiplication' _ point 1 _ _ _ = point
pointMultiplication' gen point multiplyBy a b p = pointMultiplication' gen (pointAddition gen point a b p) (multiplyBy-1) a b p

--pointMultiplication''
--apply ExpToBitVector on multiplicand
--calculate 2^n*G up to ceiling . log o
--filter on bit vector
--

pointAddition :: ECPoint -> ECPoint -> Integer -> Integer -> Integer -> ECPoint
pointAddition O p2 _ _ _ = p2
pointAddition p1 O _ _ _ = p1
pointAddition p1 p2 a b p
	| x0/=x1 && y0==y1 = O
	| x0/=x1 && y1==0 = O
	| otherwise = Point (x2, y2)
	where 	Point (x0, y0) = p1
		Point (x1, y1) = p2
		lambda = lambdaf x0 x1 y0 y1 a p
		x2 = (lambda^2-(x0+x1)) `mod` p
		y2 = ((lambda*(x1-x2))-y1) `mod` p

lambdaf :: Integer -> Integer -> Integer -> Integer -> Integer -> Integer -> Integer
lambdaf x0 x1 y0 y1 a p = if x0==x1
			then ((3*(x1^2)+a)*invy) `mod` p
			else ((y0-y1)*invx) `mod` p
			where invx = multiplicativeInverse (x0-x1) p
			      invy = multiplicativeInverse (2*y1) p

pointDivision :: ECPoint -> ECPoint -> Integer -> Integer -> Integer -> Integer
pointDivision quotient point a b p = pointDivision' quotient quotient point a b p 1

pointDivision' :: ECPoint -> ECPoint -> ECPoint -> Integer -> Integer -> Integer -> Integer -> Integer
pointDivision' _    O 	     _        _ _ _ _ = 0
pointDivision' orig point    quotient a b p accum
	| point == quotient = accum
	| otherwise = pointDivision' orig newPoint quotient a b p accum+1
	where newPoint = pointAddition orig point a b p
