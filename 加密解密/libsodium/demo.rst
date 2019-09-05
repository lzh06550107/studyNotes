<?php
print "<h1>PHP Encryption with libsodium</h1>";
$message    = "This text is secret";
$ciphertext = cryptor::encrypt("password", $message);
$plaintext  = cryptor::decrypt("password", $ciphertext);
print "Message:<br />$message <br /><br />Ciphertext:<br />$ciphertext<br /><br />Plaintext:<br />$plaintext";
/**************************************************************************************************
 * Class: cryptor
 * static class for encryption with libsodium (standard lib >= php7.2)
 *
 * Usage:
 * $message    = "This text is secret";
 * $ciphertext = cryptor::encrypt("password", $message);
 * $plaintext  = cryptor::decrypt("password", $ciphertext);
 ***************************************************************************************************/
class cryptor {

    /**
     * public gist:
     * https://gist.github.com/petermuller71/33616d55174d9725fc00a663d30194ba
     *
     * @param      string       $message          Text, to be encrypted
     * @param      string       $ciphertext       Text, to be decrypted
     * @param      string       $secret           Secret_key (hashvalue is created from this string(+salt) in order to get a 32bytes key).
     *
     * @return     string       Encrypted or decrypted text
     *
     * @license    http://www.opensource.org/licenses/mit-license.html  MIT License
     * @copyright  2018 Peter Muller. All rights reserved.
     * @author     Peter Muller <petermuller71@gmail.com>
     * @version    2.02
     *
     */

    static private $salt        = "salt";       # change
    static private $hashlength  = 30;           # change between 10 and 64 (64 = length of sodium_crypto_generichash)

    /*
     * encrypt
     *
     * @param   string    $secret      (password)
     * @param   string    $plaintext   (plaintext)
     * @return  string    Encrypted text (ciphertext + nonce + hash)
     *
     */

    public static function encrypt($secret, $plaintext) {

        // Create a 32bit password
        $key = self::create_32bit_password($secret);

        // Create a nonce: a piece of non-secret unique data that is used to randomize the cipher (safety against replay attack).
        // The nonce should be stored or shared along with the ciphertext, because the nonce needs to be reused with the same key.
        // In this class the nonce is shared with the ciphertext.
        $nonce = random_bytes(SODIUM_CRYPTO_SECRETBOX_NONCEBYTES);

        // Encrypted
        $ciphertext = bin2hex(
            sodium_crypto_secretbox($plaintext, $nonce, $key)
            );

        // Hex nonce (in order to sent together with ciphertext)
        $nonce_hex = bin2hex($nonce);

        // Create hash from ciphertext+nonce
        // It is not necessary, but just an extra layer of defense:
        // - more difficult to manipulate the string
        // - a nonce is always 48 characters. Because of a trailing hash (of unkown length), the nonce cannot be identified easily.
        //   (a nonce does not have to be secret, this is just an extra precaution)
        $hash = self::create_hash($ciphertext.$nonce_hex);
        // Return ciphertext + nonce + hash
        return $ciphertext.$nonce_hex.$hash;
    }

    /*
     * decrypt
     *
     * @param   string    $secret      (password)
     * @param   string    $ciphertext  (ciphertext + nonce + hash)
     * @return  string    decrypted text
     *
     */

    public static function decrypt($secret, $ciphertext) {

        // Create a 32bit password
        $key     = self::create_32bit_password($secret);

        //Get hash
        $hash            = substr($ciphertext,-self::$hashlength);

        //Get ciphertext + nonce (remove trailing hash)
        $ciphertext      = substr($ciphertext,0,-self::$hashlength);

        //Re-create hash
        $hash_on_the_fly = self::create_hash($ciphertext);

        //Check if hash is correct
        if ($hash !== $hash_on_the_fly)
        {
            //Do propper error handling
            return "error";
        }
        else
        {
            // Get nonce (last 48 chars of string)
            $nonce_hex  = substr($ciphertext,-48);

            // Get ciphertext (remove nonce)
            $ciphertext = substr($ciphertext,0,-48);

            // Bin nonce
            $nonce      = hex2bin($nonce_hex);

            // Decrypted
            $plaintext = sodium_crypto_secretbox_open(
                hex2bin($ciphertext), $nonce, $key
                );

            return $plaintext;
        }
    }


    /*
     * create_32bit_password
     *
     * @param   string    $secret      (password)
     * @return  string    32bit-password
     *
     */

    private static function create_32bit_password($secret)
    {
        //Openlib needs a 32bit key for encryption
        return substr( bin2hex( sodium_crypto_generichash($secret.self::$salt) ),0 ,32);
    }


    /*
     * create_hash of ciphertext+nonce
     *
     * @param   string    $ciphertext_and_nonce   (ciphertext + nonce)
     * @return  string    hash
     *
     */

    private static function create_hash($ciphertext_and_nonce)
    {
        return substr( bin2hex( sodium_crypto_generichash( $ciphertext_and_nonce ) ),0 ,self::$hashlength);
    }
}
?>