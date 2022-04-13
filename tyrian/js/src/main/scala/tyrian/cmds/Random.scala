package tyrian.cmds

import cats.Applicative
import tyrian.Cmd

/** Generate random values. */
object Random:

  private val r: scala.util.Random =
    new scala.util.Random()

  /** Random `Int` */
  def int[F[_]: Applicative]: Cmd[F, RandomValue.NextInt] =
    RandomValue.NextInt(r.nextInt).toCmd

  /** Random `Int` within an upper limit */
  def int[F[_]: Applicative](upperLimit: Int): Cmd[F, RandomValue.NextInt] =
    RandomValue.NextInt(r.nextInt(upperLimit)).toCmd

  /** Random `Long` */
  def long[F[_]: Applicative]: Cmd[F, RandomValue.NextLong] =
    RandomValue.NextLong(r.nextLong).toCmd

  /** Random `Long` within an upper limit */
  def long[F[_]: Applicative](upperLimit: Long): Cmd[F, RandomValue.NextLong] =
    RandomValue.NextLong(r.nextLong(upperLimit)).toCmd

  /** Random `Float` */
  def float[F[_]: Applicative]: Cmd[F, RandomValue.NextFloat] =
    RandomValue.NextFloat(r.nextFloat).toCmd

  /** Random `Double` */
  def double[F[_]: Applicative]: Cmd[F, RandomValue.NextDouble] =
    RandomValue.NextDouble(r.nextDouble).toCmd

  /** Random series of alphanumeric characters */
  def alphaNumeric[F[_]: Applicative](length: Int): Cmd[F, RandomValue.NextAlphaNumeric] =
    RandomValue.NextAlphaNumeric(r.alphanumeric.take(length).mkString).toCmd

  /** Randomly shuffle a list of elements */
  def shuffle[F[_]: Applicative, A](l: List[A]): Cmd[F, RandomValue.NextShuffle[A]] =
    RandomValue.NextShuffle(r.shuffle(l)).toCmd

  /** Random values produced based on a specific seed value */
  final case class Seeded(seed: Long):

    private val r: scala.util.Random =
      new scala.util.Random(seed)

    /** Random `Int` */
    def int[F[_]: Applicative]: Cmd[F, RandomValue.NextInt] =
      RandomValue.NextInt(r.nextInt).toCmd

    /** Random `Int` within an upper limit */
    def int[F[_]: Applicative](upperLimit: Int): Cmd[F, RandomValue.NextInt] =
      RandomValue.NextInt(r.nextInt(upperLimit)).toCmd

    /** Random `Long` */
    def long[F[_]: Applicative]: Cmd[F, RandomValue.NextLong] =
      RandomValue.NextLong(r.nextLong).toCmd

    /** Random `Long` within an upper limit */
    def long[F[_]: Applicative](upperLimit: Long): Cmd[F, RandomValue.NextLong] =
      RandomValue.NextLong(r.nextLong(upperLimit)).toCmd

    /** Random `Float` */
    def float[F[_]: Applicative]: Cmd[F, RandomValue.NextFloat] =
      RandomValue.NextFloat(r.nextFloat).toCmd

    /** Random `Double` */
    def double[F[_]: Applicative]: Cmd[F, RandomValue.NextDouble] =
      RandomValue.NextDouble(r.nextDouble).toCmd

    /** Random series of alphanumeric characters */
    def alphaNumeric[F[_]: Applicative](length: Int): Cmd[F, RandomValue.NextAlphaNumeric] =
      RandomValue.NextAlphaNumeric(r.alphanumeric.take(length).mkString).toCmd

    /** Randomly shuffle a list of elements */
    def shuffle[F[_]: Applicative, A](l: List[A]): Cmd[F, RandomValue.NextShuffle[A]] =
      RandomValue.NextShuffle(r.shuffle(l)).toCmd

  end Seeded

  extension [F[_]: Applicative](i: RandomValue.NextInt)
    def toCmd: Cmd[F, RandomValue.NextInt] =
      Cmd.Run(Applicative[F].pure(i.value), RandomValue.NextInt(_))

  extension [F[_]: Applicative](i: RandomValue.NextLong)
    def toCmd: Cmd[F, RandomValue.NextLong] =
      Cmd.Run(Applicative[F].pure(i.value), RandomValue.NextLong(_))

  extension [F[_]: Applicative](i: RandomValue.NextFloat)
    def toCmd: Cmd[F, RandomValue.NextFloat] =
      Cmd.Run(Applicative[F].pure(i.value), RandomValue.NextFloat(_))

  extension [F[_]: Applicative](i: RandomValue.NextDouble)
    def toCmd: Cmd[F, RandomValue.NextDouble] =
      Cmd.Run(Applicative[F].pure(i.value), RandomValue.NextDouble(_))

  extension [F[_]: Applicative](i: RandomValue.NextAlphaNumeric)
    def toCmd: Cmd[F, RandomValue.NextAlphaNumeric] =
      Cmd.Run(Applicative[F].pure(i.value), RandomValue.NextAlphaNumeric(_))

  extension [F[_]: Applicative, A](i: RandomValue.NextShuffle[A])
    def toCmd: Cmd[F, RandomValue.NextShuffle[A]] =
      Cmd.Run(Applicative[F].pure(i.value), RandomValue.NextShuffle(_))

enum RandomValue derives CanEqual:
  case NextInt(value: Int)             extends RandomValue
  case NextLong(value: Long)           extends RandomValue
  case NextFloat(value: Float)         extends RandomValue
  case NextDouble(value: Double)       extends RandomValue
  case NextAlphaNumeric(value: String) extends RandomValue
  case NextShuffle[A](value: List[A])  extends RandomValue
