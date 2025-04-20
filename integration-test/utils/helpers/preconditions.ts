import { APIRequestContext } from "playwright";


export async function createOrUpdatePage(request: APIRequestContext, pageName: string, url: string, payload: any,) {
    console.log(`Creating ${pageName}`);
    const createPageResponse = await request.post(url, {
        data: payload,
    });
    console.log(`Sending POST request to: ${createPageResponse.url()}`);
    console.log(`Response Status: ${createPageResponse.status()}`);
    console.log('Response Body:', JSON.stringify(createPageResponse.json()));

    if (createPageResponse.status() == 409) {
        console.log(`Updating ${pageName}`);
        const updatePageResponse = await request.put(url, {
            data: payload,
        });
        console.log(`Sending PUT request to: ${updatePageResponse.url()}`);
        console.log(`Response Status: ${updatePageResponse.status()}`);
        console.log('Response Body:', JSON.stringify(updatePageResponse.json()));
    }
}