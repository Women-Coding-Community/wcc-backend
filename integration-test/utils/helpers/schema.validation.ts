import Ajv, { ErrorObject } from 'ajv';
import addFormats from 'ajv-formats';

const ajv = new Ajv({
  allErrors: true, // Collect all errors, not just the first one
  verbose: true, // Provide verbose error messages
  strict: false, // Disable strict mode
});
addFormats(ajv); // Add extended formats like "uri"

/**
 * Represents a formatted error object with detailed validation information.
 * This is used to structure error details for easier reading and debugging
 * during schema validation.
 *
 * @interface FormattedError
 * @property {string} message - A descriptive message about the error.
 * @property {string} path - The path in the data where the error occurred.
 * @property {any} value - The value that caused the error.
 * @property {string} keyword - The validation keyword associated with the error (e.g., 'type', 'pattern').
 * @property {any} params - Additional parameters associated with the error, typically used for specific validations like 'enum' or 'minLength'.
 */
interface FormattedError {
  message: string;
  path: string;
  value: unknown;
  keyword: string;
  params: unknown;
}

/**
 * Validates the provided data against a specified JSON schema using AJV.
 *
 * @param schema - The JSON schema to validate the data against.
 * @param data - The data to be validated.
 *
 * @throws {Error} Throws an error if the validation fails, containing the detailed validation issues.
 *
 * @example
 * const schema = {
 *   type: "object",
 *   properties: {
 *     name: { type: "string" },
 *   },
 *   required: ["name"]
 * };
 *
 * const data = { name: 123 };  // Invalid data
 *
 * try {
 *   validateSchema(schema, data);
 * } catch (e) {
 *   console.error(e.message);  // Validation failed with detailed error information
 * }
 */
export function validateSchema<T>(schema: object, data: T): void {
  const validate = ajv.compile(schema);

  const valid = validate(data); // Validate the data

  if (!valid && validate.errors) {
    const formattedErrors = formatErrors(validate.errors);
    console.log('Detailed Errors:', JSON.stringify(formattedErrors, null, 2));
    throw new Error(`Validation failed with errors: ${JSON.stringify(formattedErrors, null, 2)}`);
  }

  console.log('Schema validated successfully!');
}

/**
 * Formats an array of AJV validation errors into a more structured and readable format.
 *
 * @param {ErrorObject[] | null} errors - The array of AJV error objects, or `null` if no errors occurred.
 * @returns {FormattedError[]} An array of formatted error objects containing detailed validation information.
 *
 * @example
 * const errors = [
 *   {
 *     message: "should be string",
 *     instancePath: "/name",
 *     keyword: "type",
 *     params: { type: "string" },
 *     data: 123
 *   }
 * ];
 *
 * const formattedErrors = formatErrors(errors);
 * console.log(formattedErrors);
 *
 * // Output:
 * // [
 * //   {
 * //     message: "should be string",
 * //     path: "/name",
 * //     value: 123,
 * //     keyword: "type",
 * //     params: { type: "string" }
 * //   }
 * // ]
 */
function formatErrors(errors: ErrorObject[] | null): FormattedError[] {
  if (!errors) {
    return []; // Return an empty array if there are no errors
  }

  return errors.map((err) => {
    return {
      message: err.message || 'Unknown error', // Use the error message or a default message
      path: err.instancePath || 'N/A', // Path where the error occurred (using 'instancePath' for AJV v7+)
      value: err.data || 'N/A', // The value that caused the error, 'N/A' if not present
      keyword: err.keyword || 'N/A', // The validation keyword (e.g., 'type', 'pattern', etc.)
      params: err.params || {}, // Additional params (e.g., expected value for enum)
    };
  });
}
