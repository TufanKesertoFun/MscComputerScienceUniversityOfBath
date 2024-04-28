module Main where

import qualified Data.ByteString.Lazy as BL
import qualified Data.ByteString.Builder as B
import System.Process
import Text.Printf

type Pulse = Float
type Seconds = Float
type Samples = Float
type Hz = Float
type Semitones = Float
type Beats = Float

outputFilePath :: FilePath
outputFilePath = "output.bin"

volume :: Float
volume = 0.2

sampleRate :: Samples
sampleRate = 48000.0

pitchStandard :: Hz
pitchStandard = 440.0

bpm :: Beats
bpm = 120.0

beatDuration :: Seconds
beatDuration = 60.0 / bpm

f :: Semitones -> Hz
f n = pitchStandard * (2 ** (1.0 / 12.0)) ** n

note :: Semitones -> Beats -> [Pulse]
note n beats = freq (f n) (beats * beatDuration)

freq :: Hz -> Seconds -> [Pulse]
freq hz duration =
  map (* volume) $ zipWith3 (\x y z -> x * y * z) release attack output
  where
    step = (hz * 2 * pi) / sampleRate

    attack :: [Pulse]
    attack = map (min 1.0) [0.0, 0.001 ..]

    release :: [Pulse]
    release = reverse $ take (length output) attack

    output :: [Pulse]
    output = map sin $ map (* step) [0.0 .. sampleRate * duration]

-- Introduce a more dynamic and varied melody
melody :: [Pulse]
melody =
  concat
    [ note 0 0.25
    , note 0 0.25
    , note 5 0.25
    , note 7 0.25
    , note 3 0.5
    , note 0 0.25
    , note 2 0.25
    , note 3 0.25
    , note 5 0.25
    , note 7 0.25
    , note 12 0.25
    , note 10 0.25
    , note 7 0.25
    , note 5 0.5
    , note 3 0.25
    , note 2 0.25
    , note 0 0.5
    ]


song :: [Pulse]
song =
  concat
    [ concatMap (const melody) [1..2]
    , concatMap (transposeMelody 7) $ concatMap (const [melody]) [1..2]
    , concatMap reverseMelody $ concatMap (const [melody]) [1..2]
    , concatMap (const melody) [1..2]
    ]

transposeMelody :: Semitones -> [Pulse] -> [Pulse]
transposeMelody transposition = map (\p -> if p /= 0 then p + transposition else p)

reverseMelody :: [Pulse] -> [Pulse]
reverseMelody = reverse

save :: FilePath -> IO ()
save filePath = BL.writeFile filePath $ B.toLazyByteString $ mconcat $ map B.floatLE song


play :: IO ()
play = do
  save outputFilePath
  _ <- runCommand $ printf "ffplay -autoexit -showmode 1 -f f32le -ar %f %s" sampleRate outputFilePath
  return ()

main :: IO ()
main = save outputFilePath
