import Text.Printf
import System.Random
import System.Environment

main :: IO ()
main = putStrLn . show $ factorSemiprime 21564415789407419016009208261248207096642906004800841795936698039956955065689470860442617778592251148497108487382255545368574894797563095580462013940139458737595347116196232020054458096952797158372048820734027669236503961399225114862324365965953643944890728726144498639023054215484104813040810616204828782873883429902944242652594399029282908767130952753073515594458968256559993945366064220719356741774466168843870890636433133117969509622752516166395557625078469156276092465034492152974335741763377371799704110154420398452914035429979733487690222654335101342728662488413245550632768166117115966745731271733452185096137

factorSemiprime :: Integer -> (Integer, Integer)
factorSemiprime semiprime =  factorSemiprime' semiprime root 
	where root = nearestOdd $ squareRoot semiprime

factorSemiprime' :: Integer -> Integer -> (Integer, Integer)
factorSemiprime' semiprime q 
	| semiprime `mod` q == 0 =  (q, semiprime `div` q)
	| otherwise = factorSemiprime' semiprime (q-2)

(^!) :: Num a => a -> Int -> a
(^!) x n = x^n
 
squareRoot :: Integer -> Integer
squareRoot 0 = 0
squareRoot 1 = 1
squareRoot n =
   let twopows = iterate (^!2) 2
       (lowerRoot, lowerN) =
          last $ takeWhile ((n>=) . snd) $ zip (1:twopows) twopows
       newtonStep x = div (x + div n x) 2
       iters = iterate newtonStep (squareRoot (div n lowerN) * lowerRoot)
       isRoot r  =  r^!2 <= n && n < (r+1)^!2
   in  head $ dropWhile (not . isRoot) iters

nearestOdd :: Integer -> Integer
nearestOdd num
	| odd num = num 
	| otherwise = num + 1

--printMultTable :: [Integer] -> Integer -> [IO ()]
--printMultTable elements modulus = map printLine . map (\element -> generateLine elements modulus element) elements

-- printTable :: [Integer] -> Integer -> ()
-- printTable elements modulus = do mapM_ printEntry elements
--			       	 printTableData $ createTableData elements modulus

-- printTable' :: [Integer] -> Integer -> ()
-- printTable' elements modulus = let tableRows = createTableData elements modulus
--	in mapM_ (mapM_ printEntry) tableRows

--printTableData :: [[Integer]] -> ()
--printTableData table = mapM_ printLine table

--createTableData :: [Integer] -> Integer -> [[Integer]]
--createTableData elements modulus = [x | a<-elements, x<-[[(a*b) `mod` modulus | b<-elements, b<=a]]]

--printLines = foldr (\x -> printLine x >>) (return ()) generateLines 

--generateLines :: [Integer] -> [Integer] -> [[Integer]]
--generateLines elements modulus = foldr (:) [] . map (generateLine elements modulus) elements

--generateLine :: [Integer] -> [Integer] -> Integer -> [Integer]
--generateLine elements modulus element = [mod((element*x) modulus) | x <- elements, element>=x]  

--generateLines :: [Integer] -> Integer -> [[Integer]]
--generateLines [] _ = []
--generateLines x:xs modulus = [mod (x*y) modulus | y<-xs, x<=y] : generateLine xs modulus

--printLine :: [Integer] -> IO ()
--printLine [] = printDivider
--printLine (x:xs) = printEntry x >> printLine xs

--printEntry :: Integer -> IO () 
--printEntry x = printf "| %4d " x

--printDivider :: IO ()
--printDivider = putStrLn "------------------------------------------------"

--exponentMod :: Integer -> Integer -> Integer -> Integer -> Integer
--exponentMod _ _ -1 = 1
--exponentMod base exp modulus x = 
--		| set == True = mod ((pow base $ 1 `shift` x) * (exponentMod exp base modulus x-1)) modulus
--		| otherwise = exponentMod base exp mod x-1
--		where set = exp `shift` (-x) .&. 1 == 1 


--printModExp base exp modulus = foldr (() >>= ) return (0) (exponenets base exp modulus)

--addAndMod  :: [Integer] -> Integer -> IO Integer
--printModExp2 exponents mod = a <- return 
--putStrLn 

--exponents :: Integer -> Integer -> Integer -> [Integer]
--exponents base exp modulus = 
--	let o = ceiling . logBase 2 x
--	f = (base `shift` (-x)) .&. 1 == 1
--	in
--	map (\exp -> (2 `pow` exp) `mod` modulus) . filter f [1..o]

--printModExp :: [Integer] -> Integer -> Integer -> IO()
--printModExp [] _ _ = return ()
--printModExp x:xs res modulus = do putStrLn res ++ " * " ++ x ++ " mod " ++ modulus ++ " = " ++ ((x*res) `mod` modulus) >> printModExp xs (x*res `mod` modulus) modulus


multiplicativeInverse' :: Integer -> Integer -> IO()
multiplicativeInverse' a m = 
	let stateTuples = extendedEuclideanAlgorithmP m a 0 1
	in printEEA stateTuples


--extendedEuclideanAlgorithm :: Integer -> Integer -> Integer -> Integer -> (Integer, Integer)
--extendedEuclideanAlgorithm x y t2 t1 =
--	| r == 0 = (y, t1)
--	| otherwise = extendedEuclideanAlgorithm y r t1 t
--	where   r = x `mod` y
--		q = x - r `div` y
--		t = t2 - t1*q

printEEA :: [(Integer, Integer, Integer, Integer, Integer, Integer, Integer)] -> IO ()
printEEA tuples = printHeader >> mapM_ (printTup) tuples >> return ()

printHeader :: IO () 
printHeader = printf "| %6s | %6s | %6s | %6s | %6s | %6s | %6s |\n" "x" "y" "t2" "t1" "r" "q" "t"

printTup :: (Integer,Integer,Integer,Integer,Integer,Integer,Integer) -> IO ()
printTup tuple = let (x, y, t2, t1, r, q, t) = tuple 
	in printf "| %6d | %6d | %6d | %6d | %6d | %6d | %6d |\n" x y t2 t1 r q t

extendedEuclideanAlgorithmP :: Integer -> Integer -> Integer -> Integer -> [(Integer, Integer, Integer, Integer, Integer, Integer, Integer)] 
extendedEuclideanAlgorithmP x y t2 t1 
	| r == 0 = [(x, y, t2, t1, r, q, t)]
	| otherwise = (x, y, t2, t1, r, q, t) : extendedEuclideanAlgorithmP y r t1 t
	where   r = x `mod` y
		q = (x - r) `div` y
		t = t2 - (t1*q)


--factor :: Integer -> [(Integer, Integer)]
--factor x = filter (\t -> snd t != 0 ) . map (\x -> 
--		| remainder == 0 = (n, q)
--		| otherwise (n, 0)
--		where remainder = n `mod` x
--	 ) [2..(floor . sqrt . fromInteger n)]
--	where q = factor2 n x

--factor2 :: Integer -> Integer -> Integer
--factor2 x divisor = (x `mod` divisor == 0) ? 1 + (factor2 (x `div` divisor) divisor) ?: 0

--totient :: Integer -> Integer
--totient q = foldr (*) 0 . map tot . factor q
--	where tot a b = (pow a b) - (pow a (b-1))

--findRSAKeypair :: Integer -> (Integer, (Integer, Integer))
--keyPair e lower upper = let keyPair = take 1 [(x,y) | x <- [lower..upper], y <- [lower..upper], prime x, prime y, coprime ((x-1) * (y-1)) e] 
--	multTuple (x, y) = x*y
--	in (multiplicativeInverse e (multTuple keyPair), keyPair)


--fermatTest :: Integer -> Integer -> Integer
--fermatTest _ 0 = -1
--fermatTest n maxTries =
--case a of
--1 -> fermatTest n maxTries -1
--_ -> a
--where a = randomInt 1 n-1

--randomInt :: Integer -> Integer -> Integer
--randomInt min max = getStdRandom (randomR (1, n-1))

--data Cond a = a :? a

--infixl 0 ? (x : ?_) = x
--infixl 1 :? (_ : ?y) = y

--(?) :: Bool -> Cond a -> a
--True ? (x :? _) = x
--False ? (_ :? y) = y
