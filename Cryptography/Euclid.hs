import Text.Printf
import Control.Monad

data EuclideanMatrix = EuclideanMatrix EuclideanRow EuclideanRow

data EuclideanRow = EuclideanRow Int Int Int

main = 
  let prompt var = do 
      putStrLn ("Please enter the value of " ++ var ++ ":")     
      c <- getLine
      return (read c :: Int)
  in do
    a <- prompt "a"
    b <- prompt "b"
    initialMatrix <- return (EuclideanMatrix (EuclideanRow a 0 1) (EuclideanRow b 1 0))
    matrixProg <- return (getMatrixProgression initialMatrix)
    mapM_ print matrixProg
    print multInverse matrixProg

instance Show EuclideanMatrix where
  show (EuclideanMatrix r1 r2) = show r1 ++ show r2

instance Show EuclideanRow where
  show (EuclideanRow a b c) = printf "| %-7d %-7d %-7d |\n" a b c

getMatrixProgression initialMatrix = let isNotDone (EuclideanMatrix (EuclideanRow a _ _) (EuclideanRow b _ _)) = a /= 0 && b /= 0 
  in  takeUntil isNotDone $ iterate euclideanRowOperation initialMatrix

euclideanRowOperation (EuclideanMatrix r1@(EuclideanRow a c e) r2@(EuclideanRow b d f)) =
  let 
    rowSubtract r1@(EuclideanRow g i k) r2@(EuclideanRow h j l) q = EuclideanRow (g-h*q) (i-j*q) (k-l*q)
  in if a>b then EuclideanMatrix (rowSubtract r1 r2 (a `quot` b)) r2 else EuclideanMatrix r1 (rowSubtract r2 r1 (b `quot` a))

takeUntil predicate (x:xs) = 
  if predicate x then x:takeUntil predicate xs else [x]
  
