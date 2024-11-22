import Ajv, {ErrorObject} from 'ajv';

interface FormattedError {
    field: string; // The path to the problematic field in the data
    issue: string; // The validation error message
}

function formatErrors(errors: ErrorObject[]): FormattedError[] {
    return errors.map((error) => ({
        field: error.instancePath,
        issue: error.message || "Unknown error",
    }));
}

export function validateSchema(schema: any, data: any) {
    const ajv = new Ajv(); // Create a new AJV instance
    const validate = ajv.compile(schema); // Compile the schema

    const valid = validate(data); // Validate the data

    if (!!valid && validate.errors) {
        const formattedErrors = formatErrors(validate.errors);
        console.log("Formatted Errors:", formattedErrors);
    }
    console.log("Correct schema")
}

